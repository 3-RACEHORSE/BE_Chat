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
    NO_DATA(404, "데이터가 없습니다"),
    WRONG_CHATROOM_AND_MEMBER(400, "잘못된 채팅방과 사용자입니다"),
    NOT_ENOUGH_MEMBERS(400, "최소 두명 이상이 있어야합니다."),
    INTERNAL_SERVER_ERROR(500, "서버 오류 발생"),
    SAVE_CHAT_FAILED(500, "채팅 저장 실패"),
    LOAD_CHAT_FAILED(500,"채팅 불러오기 실패"),
    CREATE_CHATROOM_FAILED(500,"채팅방 생성 실패"),
    CANNOT_FIND_CHATROOM(500,"채팅방 찾기 실패"),
    SAVE_REVIEW_FAILED(500, "리뷰 저장 실패");


    private final int code;
    private final String message;
}