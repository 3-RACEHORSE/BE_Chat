package com.skyhorsemanpower.chatService.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ResponseStatus {

    /**
     * 200: 요청 성공
     **/
    SUCCESS(200, "요청에 성공했습니다."),
    //
//    /**
//     * 400 : security 에러
//     */
//    WRONG_JWT_TOKEN(false, 401, "다시 로그인 해주세요"),
//
//    /**
//     * 900: 기타 에러
//     */
    INTERNAL_SERVER_ERROR(500, "Internal server error");
    private final int code;
    private final String message;

}