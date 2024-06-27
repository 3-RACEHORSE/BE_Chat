package com.skyhorsemanpower.chatService.chat.data.vo;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class LastChatVo {
    private String content;
    private LocalDateTime createdAt;
    private String roomNumber;
    @Builder
    public LastChatVo(String content, LocalDateTime createdAt, String roomNumber) {
        this.content = content;
        this.createdAt = createdAt;
        this.roomNumber = roomNumber;
    }

    public LastChatVo() {

    }
}
