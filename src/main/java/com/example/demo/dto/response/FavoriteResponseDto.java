package com.example.demo.dto.response;

import com.example.demo.domain.Favorite;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class FavoriteResponseDto {
    private Long id;
    private String newsTitle;
    private String newsSummary;
    private String newsLink;
    private String newsThumbnail;
    private String newsCategory;

    public static FavoriteResponseDto from(Favorite fav) {
        return FavoriteResponseDto.builder()
                .id(fav.getId())
                .newsTitle(fav.getNewsTitle())
                .newsSummary(fav.getNewsSummary())
                .newsLink(fav.getNewsLink())
                .newsThumbnail(fav.getNewsThumbnail())
                .newsCategory(fav.getNewsCategory())
                .build();
    }
    
}
