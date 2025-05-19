package com.example.demo.controller;

import java.util.List;

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
public class FavoriteController {
    private final FavoriteService favoriteService;

    // 좋아요 등록
    @PostMapping("/favorite")
    public ResponseEntity<FavoriteResponseDto> likeNews(@RequestBody FavoriteRequestDto dto) {
        FavoriteResponseDto response = favoriteService.likeNews(dto);
        return ResponseEntity.ok(response);
    }
    
    // 내가 좋아요 누른 뉴스 조회
    @GetMapping("/{userId}/favorites")
    public ResponseEntity<List<FavoriteResponseDto>> getFavorites(@PathVariable Long userId) {
        List<FavoriteResponseDto> list = favoriteService.getMyFavorites(userId);
        return ResponseEntity.ok(list);
    }
    
    // 좋아요 취소
    @DeleteMapping("/favorite/{id}")
    public ResponseEntity<Void> cancelLike(@PathVariable Long id){
        favoriteService.cancelLike(id);
        return ResponseEntity.noContent().build();
    }
}
