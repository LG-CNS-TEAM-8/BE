package com.example.demo.controller;

import com.example.demo.domain.Role;
import com.example.demo.domain.User;
import com.example.demo.service.AuthService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest body){
        String email = body.getEmail();
        String password = body.getPassword();
        return authService.login(email, password);
    }

    @PostMapping("/sign-up")
    public String signUp(@RequestBody SignUpRequest body){
        User user = User.builder()
                .email(body.getEmail())
                .name(body.getName())
                .password(body.getPassword())
                .build();
        return authService.signUp(user);
    }
}

@Data
class LoginRequest {
    private String email;
    private String password;
}

@Data
class SignUpRequest {
    private String email;
    private String password;
    private String name;
    private String role = "ROLE_USER";
}

@Data
@AllArgsConstructor
class AuthResponse {
    private String token;
}
