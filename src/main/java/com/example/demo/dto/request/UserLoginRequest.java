package com.example.demo.dto.request;

import lombok.Getter;

@Getter
public class UserLoginRequest {
    private String email;
    private String password;
}
