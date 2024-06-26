package com.skyhorsemanpower.chatService.common.kafka;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Topics {
    MEMBER_SERVICE(Constant.MEMBER_SERVICE),
    SEND_TO_CHAT(Constant.SEND_TO_CHAT);

    public static class Constant {

        public static final String SEND_TO_CHAT = "send-to-chat-topic";
        public static final String MEMBER_SERVICE = "alarm-topic";
        public static final String CHANGE_PROFILE_IMAGE = "change-profile-image-topic";
    }

    private final String topic;
}
