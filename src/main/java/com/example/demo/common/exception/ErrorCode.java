package com.example.demo.common.exception;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    //예시 오류입니다.
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류입니다.", "500"),
    USER_DUPLICATED_ID(CONFLICT, "이미 가입된 아이디입니다.", "004"),
    NEWS_PARSING_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "뉴스 정보를 불러올 수 없습니다.", "1004"),
    UNAUTHORIZED_USER(HttpStatus.UNAUTHORIZED,"인증되지 않은 사용자입니다.","401"),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED,"토큰이 만료되었습니다.","401"),

    USER_NOT_FOUND(NOT_FOUND, "사용자를 찾을 수 없습니다.", "40401"),
    FAVORITE_NOT_FOUND(NOT_FOUND, "좋아요를 찾을 수 없습니다.", "40402"),
    DUPLICATE_INTEREST(CONFLICT, "이미 존재하는 관심사입니다.", "409"),

    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "올바르지 못한 비밀번호 입니다.", "400")
    ;

    private final HttpStatus status;
    private final String message;
    private final String errorCode;


    public static CustomException userDuplicatedId() {
        return new CustomException(USER_DUPLICATED_ID);
    }

    public static CustomException internalServerError() {
        return new CustomException(INTERNAL_SERVER_ERROR);
    }

    public static CustomException newsParsingError() {
        return new CustomException(NEWS_PARSING_ERROR);
    }


    public static CustomException userNotFound() {
        return new CustomException(USER_NOT_FOUND);
    }
    public static CustomException favoriteNotFound() {
        return new CustomException(FAVORITE_NOT_FOUND);
    }
    public static CustomException duplicateInterest() {
        return new CustomException(DUPLICATE_INTEREST);
    }
}

