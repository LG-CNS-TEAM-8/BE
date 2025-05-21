package com.example.demo.dto.request;

import com.example.demo.domain.Role;
import lombok.Getter;

import java.util.List;

@Getter
public class UserSignUpRequest {
    private String email;
    private String password;
    private String name;
    private Role role = Role.USER;
}
