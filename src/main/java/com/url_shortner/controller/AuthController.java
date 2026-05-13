package com.url_shortner.controller;

import com.url_shortner.dto.LoginResponse;
import com.url_shortner.dto.RegisterResponse;
import com.url_shortner.security.jwt.JwtAuthenticationResponse;
import com.url_shortner.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    @PostMapping("/public/register")
    public ResponseEntity<RegisterResponse> registerUser(@RequestBody RegisterResponse response){
        RegisterResponse registerUser = userService.registerUser(response);
        return ResponseEntity.status(HttpStatus.CREATED).body(registerUser);
    }

    @PostMapping("public/login")
    public ResponseEntity<JwtAuthenticationResponse> loginUser(@RequestBody LoginResponse loginResponse){
        JwtAuthenticationResponse response = userService.loginUser(loginResponse);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
