package com.example.demo.dto.request;

import lombok.Data;

@Data
class LoginRequest {
    private String email;
    private String password;
}

