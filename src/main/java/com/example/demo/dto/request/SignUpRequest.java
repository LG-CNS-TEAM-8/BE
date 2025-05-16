package com.example.demo.dto.request;

import com.example.demo.domain.Role;
import lombok.Data;

@Data
class SignUpRequest {
    private String email;
    private String password;
    private String name;
    private Role role = Role.valueOf("ROLE_USER");
}

