package com.example.demo.dto.response;

import com.example.demo.domain.News;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@Builder
public class NewsResponse {
    private String title;
    private String summary;
    private String link;
    private String thumbnail;
    private String category;

    public static NewsResponse from(News news) {
        return NewsResponse.builder()
                .title(news.getTitle())
                .summary(news.getSummary())
                .link(news.getLink())
                .thumbnail(news.getThumbnail())
                .category(news.getCategory())
                .build();
    }
}
