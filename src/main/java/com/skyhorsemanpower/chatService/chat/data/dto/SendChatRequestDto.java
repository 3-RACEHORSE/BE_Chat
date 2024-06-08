package com.skyhorsemanpower.chatService.chat.data.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class SendChatRequestDto {
    private String content;
    private String roomNumber;

    @Builder
    public SendChatRequestDto(String content, String roomNumber) {
        this.content = content;
        this.roomNumber = roomNumber;
    }
}
