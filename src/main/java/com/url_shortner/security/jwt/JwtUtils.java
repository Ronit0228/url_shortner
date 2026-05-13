package com.url_shortner.security.jwt;

import com.url_shortner.services.UserDetailsImpl;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtUtils {

    @Value("${jwt.secretKey}")
    private String SECRET_KEY;

    @Value("${jwt.expiration}")
    private Long EXPIRATION_TIME_JWT;

    public Key getSecretKey(){
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY));
    }

    public String generateToken(UserDetailsImpl userDetails){
        String email = userDetails.getEmail();
        String roles = userDetails
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority).
                collect(Collectors.joining(","));

        return Jwts
                .builder()
                .subject(email)
                .claim("roles", roles)
                .signWith(getSecretKey())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME_JWT))
                .compact();
    }

    public String getJwtFromHeader(HttpServletRequest httpServletRequest){
        String bearerToken = httpServletRequest.getHeader("Authorization");
        if(bearerToken != null && bearerToken.startsWith("Bearer ")) return bearerToken.substring(7);
        return null;
    }

    public String getUserNameFromToken(String jwtToken) {
        return Jwts
                .parser()
                .verifyWith((SecretKey) getSecretKey())
                .build()
                .parseSignedClaims(jwtToken)
                .getPayload().getSubject();
    }

    public boolean validateToken(String authToken){
        try {
            Jwts
                    .parser()
                    .verifyWith((SecretKey) getSecretKey())
                    .build()
                    .parseSignedClaims(authToken);

            return true;
        } catch (JwtException e){
            throw new RuntimeException(e);
        } catch (IllegalArgumentException e){
            throw new RuntimeException(e + "Wrong Jwt");
        }
    }
}
