package com.example.demo.controller;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.request.FavoriteRequestDto;
import com.example.demo.dto.response.FavoriteResponseDto;
import com.example.demo.service.FavoriteService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequiredArgsConstructor
@Tag(name = "Favorite API", description = "뉴스 좋아요 관련 API")
public class FavoriteController {
    private final FavoriteService favoriteService;

    @Operation(summary = "뉴스 좋아요 등록", description = "사용자가 특정 뉴스에 좋아요를 등록합니다.")
    @PostMapping("/favorite")
    public ResponseEntity<?> likeNews(@RequestBody FavoriteRequestDto dto) {
        return favoriteService.likeNews(dto)
            .<ResponseEntity<?>>map(ResponseEntity::ok)
            .orElseGet(()-> ResponseEntity.noContent().build());
    }

    @Operation(summary = "좋아요한 뉴스 조회", description = "특정 사용자가 좋아요를 누른 뉴스 목록을 조회합니다.")
    @GetMapping("/{userId}/favorites")
    public ResponseEntity<List<FavoriteResponseDto>> getFavorites(@PathVariable Long userId) {
        List<FavoriteResponseDto> list = favoriteService.getMyFavorites(userId);
        return ResponseEntity.ok(list);
    }

    @Operation(summary = "뉴스 좋아요 취소", description = "좋아요를 누른 뉴스에 대해 좋아요를 취소합니다.")
    @DeleteMapping("/favorite/{id}")
    public ResponseEntity<Void> cancelLike(@PathVariable Long id){
        favoriteService.cancelLike(id);
        return ResponseEntity.noContent().build();
    }
}
