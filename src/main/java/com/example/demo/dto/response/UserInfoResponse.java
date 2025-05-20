package com.example.demo.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserInfoResponse {
    private String email;
    private String name;
}
