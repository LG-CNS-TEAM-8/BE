package com.example.demo.controller;

import com.example.demo.dto.response.NewsResponse;
import com.example.demo.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class NewsController {
    private final NewsService newsService;
    /***
     * 헤드라인 뉴스들 응답 Api
     * @return List<NewsResponseDto>
     */
    @GetMapping("/news")
    public ResponseEntity<List<NewsResponse>> getNews() {
        List<NewsResponse> newsList = newsService.getNews();
        return ResponseEntity.ok(newsList);
    }

    /***
     * 사용자 검색어로 뉴스 검색 API
     * @param keyword
     * @return List<NewsResponseDto>
     */
    @GetMapping("/news/{keyword}")
    public ResponseEntity<List<NewsResponse>> searchNews(@PathVariable String keyword) {
        List<NewsResponse> newsList = newsService.searchNews(keyword);
        return ResponseEntity.ok(newsList);
    }

    /***
     *
     */
    @GetMapping("/ai")
    public ResponseEntity<List<NewsResponse>> getPrompt() {
        String prompt = newsService.getPrompt();
        List<NewsResponse> response = newsService.getResponse(prompt,50);
        return ResponseEntity.ok(response);
    }
}
