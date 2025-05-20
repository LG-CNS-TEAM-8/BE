package com.example.demo.controller;

import com.example.demo.domain.User;
import com.example.demo.dto.request.UserLoginRequest;
import com.example.demo.dto.request.UserSignUpRequest;
import com.example.demo.dto.response.CreateNewAccessTokenResponse;
import com.example.demo.dto.response.UserLoginResponse;
import com.example.demo.dto.response.UserSignUpResponse;
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
    public ResponseEntity<UserSignUpResponse> signUp(@RequestBody UserSignUpRequest body){
        User user = User.builder()
                .email(body.getEmail())
                .name(body.getName())
                .password(body.getPassword())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.signUp(user));
    }

    @GetMapping("/email")
    public ResponseEntity<CheckEmailResponse> checkEmail(@RequestBody String email){
        return ResponseEntity.ok(authService.isEmailExist(email));
    }

    @PostMapping("/token")
    public ResponseEntity<CreateNewAccessTokenResponse> createNewAccessToken(@RequestHeader(value = "refreshToken") String refreshToken){
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.createNewAccessToken(refreshToken));
    }

    @DeleteMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader(value = "refreshToken") String refreshToken){
        authService.deleteRefreshToken(refreshToken);
        return ResponseEntity.ok("Logout Successful");
    }
}
