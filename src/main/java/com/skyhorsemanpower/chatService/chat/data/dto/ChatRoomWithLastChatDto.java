package com.skyhorsemanpower.chatService.chat.data.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
public class ChatRoomWithLastChatDto {
    private String roomNumber;
    private String content;
    private LocalDateTime lastChatTime;

    public ChatRoomWithLastChatDto(String roomNumber, String content, LocalDateTime lastChatTime) {
        this.roomNumber = roomNumber;
        this.content = content;
        this.lastChatTime = lastChatTime;
    }
}
