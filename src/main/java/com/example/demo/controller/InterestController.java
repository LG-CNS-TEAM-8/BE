package com.example.demo.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.request.InterestRequestDto;
import com.example.demo.dto.response.InterestResponseDto;
import com.example.demo.service.InterestService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequiredArgsConstructor
public class InterestController {
    private final InterestService interestService;

    // 관심사 등록
    @PostMapping("/interest")
    public ResponseEntity<Void> addInterest(@RequestBody InterestRequestDto dto) {
        interestService.addInterest(dto);
        return ResponseEntity.ok().build();
    }

    // 관심사 조회
    @GetMapping("/interest/{userId}")
    public ResponseEntity<List<InterestResponseDto>> getInterests(@PathVariable Long userId) {
        List<InterestResponseDto> list = interestService.getInterests(userId);
        return ResponseEntity.ok(list);
    }

    // 관심사 삭제
    @DeleteMapping("/interest/delete/{userId}")
    public ResponseEntity<Void> removeInterest(
            @PathVariable Long userId,
            @RequestBody InterestRequestDto dto) {
        interestService.removeInterest(dto);
        return ResponseEntity.noContent().build();
    }
}
