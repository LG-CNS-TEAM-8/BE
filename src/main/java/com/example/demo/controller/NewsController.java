package com.example.demo.controller;

import com.example.demo.common.exception.CustomException;
import com.example.demo.common.exception.ErrorCode;
import com.example.demo.dto.request.NewsSummaryRequest;
import com.example.demo.dto.response.NewsResponse;
import com.example.demo.dto.response.NewsSummaryResponse;
import com.example.demo.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @GetMapping("/news/search/{keyword}")
    public ResponseEntity<List<NewsResponse>> searchNews(@PathVariable String keyword) {
        List<NewsResponse> newsList = newsService.getSearchNews(keyword);
        return ResponseEntity.ok(newsList);
    }

    /***
     * AI 추천기사 API
     */
    @GetMapping({"/news/ai", "/news/ai/{start}"})
    public ResponseEntity<List<NewsResponse>> getPrompt(@PathVariable(required = false) Integer start) {
        String prompt = newsService.getKeyword();
        List<NewsResponse> response = newsService.getResponse(prompt, start);
        return ResponseEntity.ok(response);
    }

    /***
     * AI 본문 요약 API
     * @return
     */
    @PostMapping("/news/summary")
    public ResponseEntity<NewsSummaryResponse> getSummary(@RequestBody NewsSummaryRequest request){
        NewsSummaryResponse response = newsService.getSummary(request.getLink());
        if(response == null) throw new CustomException(ErrorCode.NEWS_PARSING_ERROR);
        return ResponseEntity.ok(response);
    }
}
