package com.example.demo.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FavoriteRequestDto {
    private Long userId;
    private String newsTitle;
    private String newsSummary;
    private String newsLink;
    private String newsThumbnail;
    private String newsCategory;
    
}
