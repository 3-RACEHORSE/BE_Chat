package com.skyhorsemanpower.chatService.chat.data.vo;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UnReadChatCountResponseVo {
    private Long count;
}
