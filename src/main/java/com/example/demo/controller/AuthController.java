package com.example.demo.controller;

import com.example.demo.dto.request.CheckEmailRequest;
import com.example.demo.dto.request.UserLoginRequest;
import com.example.demo.dto.request.UserSignUpRequest;
import com.example.demo.dto.response.CheckEmailResponse;
import com.example.demo.dto.response.CreateNewAccessTokenResponse;
import com.example.demo.dto.response.UserLoginResponse;
import com.example.demo.dto.response.UserSignUpResponse;
import com.example.demo.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Auth API", description = "사용자 인증/인가 관련 API")
public class AuthController {
    private final AuthService authService;

    @Operation(summary = "사용자 로그인", description = "이메일과 비밀번호를 입력받아 JWT 액세스/리프레시 토큰을 반환합니다.")
    @PostMapping("/login")
    public ResponseEntity<UserLoginResponse> login(@RequestBody UserLoginRequest request){
        return ResponseEntity.ok(authService.login(request));
    }

    @Operation(summary = "회원가입", description = "이름, 이메일, 비밀번호를 입력받아 회원가입을 진행합니다.")
    @PostMapping("/sign-up")
    public ResponseEntity<UserSignUpResponse> signUp(@RequestBody UserSignUpRequest request){
        return ResponseEntity.ok(authService.signUp(request));
    }

    @Operation(summary = "이메일 중복 확인", description = "이메일이 이미 존재하는지 확인합니다.")
    @GetMapping("/email")
    public ResponseEntity<CheckEmailResponse> checkEmail(@RequestBody CheckEmailRequest request){
        return ResponseEntity.ok(authService.isEmailExist(request.getEmail()));
    }

    @Operation(summary = "새로운 액세스 토큰 발급", description = "리프레시 토큰을 통해 새로운 액세스 토큰을 발급받습니다.")
    @PostMapping("/token")
    public ResponseEntity<CreateNewAccessTokenResponse> createNewAccessToken(@RequestHeader(value = "refreshToken") String refreshToken){
        return ResponseEntity.ok(authService.createNewAccessToken(refreshToken));
    }

    @Operation(summary = "로그아웃", description = "리프레시 토큰을 삭제하여 로그아웃을 처리합니다.")
    @DeleteMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader(value = "refreshToken") String refreshToken){
        authService.deleteRefreshToken(refreshToken);
        return ResponseEntity.ok("Logout Successful");
    }
}
