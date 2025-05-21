package com.example.demo.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class UserInfoDto {
    private String email;
    private String name;
}
