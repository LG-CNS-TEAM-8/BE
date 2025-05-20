package com.example.demo.dto.response;

import com.example.demo.domain.Interest;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterestResponseDto {
    private Long id;
    private String name;

    public static InterestResponseDto from(Interest interest) {
        return InterestResponseDto.builder()
                .id(interest.getId())
                .name(interest.getName())
                .build();
    }
}
