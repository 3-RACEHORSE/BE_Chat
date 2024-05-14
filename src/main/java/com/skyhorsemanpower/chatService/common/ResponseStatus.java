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
    BAD_REQUEST(400, "최소 두명 이상이 있어야합니다."),
    INTERNAL_SERVER_ERROR(500, "Internal server error"),
    SAVE_CHAT_FAILED(500, "채팅 저장 실패"),
    LOAD_CHAT_FAILED(500,"채팅 불러오기 실패");
    private final int code;
    private final String message;

}