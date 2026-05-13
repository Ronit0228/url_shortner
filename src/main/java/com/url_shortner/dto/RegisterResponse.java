package com.url_shortner.dto;


import lombok.Data;

import java.util.List;

@Data
public class RegisterResponse {
    private String username;
    private String email;
    private String password;
    private String roles = "ROLE_USER";
}
