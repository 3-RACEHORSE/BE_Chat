package com.skyhorsemanpower.chatService.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ServerPathEnum {
    GET_MEMBER_INFO("${get_member_info}");
    private final String server;
    public static class Constant {
        public static final String AUCTION_POST_SERVER = "${auction_post_server}";
        public static final String AUCTION_INFO = "${auction_info}";
    }
}