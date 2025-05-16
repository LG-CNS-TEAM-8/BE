package com.example.demo.controller;

import com.example.demo.domain.User;
import com.example.demo.dto.*;
import com.example.demo.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponse> login(@RequestBody UserLoginRequest body){
        String email = body.getEmail();
        String password = body.getPassword();
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.login(email, password));
    }

    @PostMapping("/sign-up")
    public ResponseEntity<String> signUp(@RequestBody UserSignUpRequest body){
        User user = User.builder()
                .email(body.getEmail())
                .name(body.getName())
                .password(body.getPassword())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.signUp(user));
    }

    @PostMapping("/token")
    public ResponseEntity<CreateNewAccessTokenResponse> createNewAccessToken(@RequestHeader(value = "refreshToken") String refreshToken){
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.createNewAccessToken(refreshToken));
    }

    @GetMapping("/test")
    public String test(){
        return "test";
    }
}
