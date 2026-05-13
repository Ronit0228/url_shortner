package com.url_shortner.services;

import com.url_shortner.CustomException.UserAlreadyExistsException;
import com.url_shortner.dto.LoginResponse;
import com.url_shortner.dto.RegisterResponse;
import com.url_shortner.models.User;
import com.url_shortner.repository.UserRepository;
import com.url_shortner.security.jwt.JwtAuthenticationResponse;
import com.url_shortner.security.jwt.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ModelMapper mapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @Transactional
    public RegisterResponse registerUser(RegisterResponse response){

        if(userRepository.existsByEmail(response.getEmail())){
            throw new UserAlreadyExistsException("Email already registered: " + response.getEmail());
        }

        User user = new User();
        user.setUsername(response.getUsername());
        user.setEmail(response.getEmail());
        user.setPassword(passwordEncoder.encode(response.getPassword()));
        user.setRoles("ROLE_USER");

        User saveUser = userRepository.save(user);

        return mapper.map(saveUser, RegisterResponse.class);
    }

    public JwtAuthenticationResponse loginUser(LoginResponse loginResponse){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginResponse.getEmail(), loginResponse.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String jwtToken = jwtUtils.generateToken(userDetails);

        return new JwtAuthenticationResponse(jwtToken);
    }

    public User findByUsername(String name){
        return userRepository
                .findByUsername(name)
                .orElseThrow(
                        () -> new UsernameNotFoundException("User not found with : " + name)
                );
    }

    public User findByEmail(String email){
        return userRepository
                .findByEmail(email)
                .orElseThrow(
                        () -> new UsernameNotFoundException("User not found with email: " + email)
                );
    }
}
