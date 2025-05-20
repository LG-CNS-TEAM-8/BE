package com.example.demo.dto.request;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class InterestRequestDto {
    private Long userId;
    private List<String> name;
    
}
