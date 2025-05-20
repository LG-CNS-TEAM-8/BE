package com.example.demo.controller;

import com.example.demo.dto.response.UserInfoResponse;
import com.example.demo.security.PrincipalDetails;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/user/{id}")
    public ResponseEntity<UserInfoResponse> getUser(@PathVariable Long id){
        return ResponseEntity.ok(userService.getUserInfo(id));
    }

    @DeleteMapping("/user")
    public ResponseEntity<String> deleteUser(@AuthenticationPrincipal PrincipalDetails principalDetails){
        return ResponseEntity.ok(userService.deleteUser(principalDetails.getUsername()));
    }
}
