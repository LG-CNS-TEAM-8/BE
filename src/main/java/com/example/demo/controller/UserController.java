package com.example.demo.controller;

import com.example.demo.dto.response.UserInfoDto;
import com.example.demo.security.PrincipalDetails;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/user/{id}")
    public ResponseEntity<UserInfoDto> getUser(@PathVariable Long id){
        return ResponseEntity.ok(userService.getUserInfo(id));
    }

    @PutMapping("/user")
    public ResponseEntity<UserInfoDto> updateUser(@AuthenticationPrincipal PrincipalDetails principalDetails, @RequestBody UserInfoDto request){
        return ResponseEntity.ok(userService.updateUserInfo(request, principalDetails.getId()));
    }

    @DeleteMapping("/user")
    public ResponseEntity<String> deleteUser(@AuthenticationPrincipal PrincipalDetails principalDetails){
        return ResponseEntity.ok(userService.deleteUser(principalDetails.getId()));
    }
}
