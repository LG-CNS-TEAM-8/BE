package com.example.demo.controller;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Interest API", description = "사용자 관심사 관련 API")
public class InterestController {
    private final InterestService interestService;

    @Operation(summary = "사용자 관심사 등록", description = "사용자의 관심사를 등록합니다.")
    @PostMapping("/interest")
    public ResponseEntity<Void> addInterest(@RequestBody InterestRequestDto dto) {
        interestService.addInterest(dto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "사용자 관심사 조회", description = "특정 사용자의 관심사 목록을 조회합니다.")
    @GetMapping("/interest/{userId}")
    public ResponseEntity<List<InterestResponseDto>> getInterests(@PathVariable Long userId) {
        List<InterestResponseDto> list = interestService.getInterests(userId);
        return ResponseEntity.ok(list);
    }

    @Operation(summary = "사용자 관심사 삭제", description = "특정 사용자의 관심사를 삭제합니다.")
    @DeleteMapping("/interest/delete/{userId}")
    public ResponseEntity<Void> removeInterest(
            @PathVariable Long userId,
            @RequestBody InterestRequestDto dto) {
        dto.setUserId(userId);
        interestService.removeInterest(dto);
        return ResponseEntity.noContent().build();
    }
}
