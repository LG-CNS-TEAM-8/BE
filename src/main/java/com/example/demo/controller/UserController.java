package com.example.demo.controller;

import com.example.demo.dto.response.UserInfoDto;
import com.example.demo.security.PrincipalDetails;
import com.example.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "User API", description = "사용자 정보 관련 API")
public class UserController {
    private final UserService userService;

    @Operation(summary = "사용자 정보 조회", description = "사용자의 ID를 기반으로 사용자 정보를 조회합니다.")
    @GetMapping("/user/{id}")
    public ResponseEntity<UserInfoDto> getUser(@PathVariable Long id){
        return ResponseEntity.ok(userService.getUserInfo(id));
    }

    @Operation(summary = "사용자 정보 수정", description = "현재 로그인된 사용자의 정보를 수정합니다.")
    @PutMapping("/user")
    public ResponseEntity<UserInfoDto> updateUser(
            @Parameter(hidden = true) @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestBody UserInfoDto request){
        return ResponseEntity.ok(userService.updateUserInfo(request, principalDetails.getId()));
    }

    @Operation(summary = "회원 탈퇴", description = "현재 로그인된 사용자의 계정을 탈퇴 처리합니다.")
    @DeleteMapping("/user")
    public ResponseEntity<String> deleteUser(
            @Parameter(hidden = true) @AuthenticationPrincipal PrincipalDetails principalDetails){
        return ResponseEntity.ok(userService.deleteUser(principalDetails.getId()));
    }
}
