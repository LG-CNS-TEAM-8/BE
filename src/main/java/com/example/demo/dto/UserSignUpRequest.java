package com.example.demo.dto;

import com.example.demo.domain.Role;
import lombok.Getter;

@Getter
public class UserSignUpRequest {
    private String email;
    private String password;
    private String name;
    private Role role = Role.valueOf("USER");
}
