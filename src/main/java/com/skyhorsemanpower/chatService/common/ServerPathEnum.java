package com.skyhorsemanpower.chatService.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ServerPathEnum {
    MEMBER_SERVER("http://52.79.127.196:8000/member-service"),
    GET_MEMBER_INFO("/api/v1/non-authorization/users/datarequest/with-uuid");
    private final String server;
}