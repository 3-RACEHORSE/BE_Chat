package com.skyhorsemanpower.chatService.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum JsonPropertyEnum {
    HANDLE(Constant.HANDLE),
    PROFILE(Constant.PROFILE);

    public static class Constant {
        public static final String HANDLE = "handle";
        public static final String PROFILE = "profileImage";
    }

    private final String property;
}