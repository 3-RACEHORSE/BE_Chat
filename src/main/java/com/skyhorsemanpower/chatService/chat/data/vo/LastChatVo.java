package com.skyhorsemanpower.chatService.chat.data.vo;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class LastChatVo {
    private String content;
    private LocalDateTime createdAt;
    @Builder
    public LastChatVo(String content, LocalDateTime createdAt) {
        this.content = content;
        this.createdAt = createdAt;
    }

    public LastChatVo() {

    }
}
