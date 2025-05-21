package com.example.demo.controller;

import com.example.demo.dto.request.CheckEmailRequest;
import com.example.demo.dto.request.UserLoginRequest;
import com.example.demo.dto.request.UserSignUpRequest;
import com.example.demo.dto.response.CheckEmailResponse;
import com.example.demo.dto.response.CreateNewAccessTokenResponse;
import com.example.demo.dto.response.UserLoginResponse;
import com.example.demo.dto.response.UserSignUpResponse;
import com.example.demo.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponse> login(@RequestBody UserLoginRequest request){
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/sign-up")
    public ResponseEntity<UserSignUpResponse> signUp(@RequestBody UserSignUpRequest request){
        return ResponseEntity.ok(authService.signUp(request));
    }

    @GetMapping("/email")
    public ResponseEntity<CheckEmailResponse> checkEmail(@RequestBody CheckEmailRequest request){
        return ResponseEntity.ok(authService.isEmailExist(request.getEmail()));
    }

    @PostMapping("/token")
    public ResponseEntity<CreateNewAccessTokenResponse> createNewAccessToken(@RequestHeader(value = "refreshToken") String refreshToken){
        return ResponseEntity.ok(authService.createNewAccessToken(refreshToken));
    }

    @DeleteMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader(value = "refreshToken") String refreshToken){
        authService.deleteRefreshToken(refreshToken);
        return ResponseEntity.ok("Logout Successful");
    }
}
