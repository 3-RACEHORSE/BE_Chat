package com.skyhorsemanpower.chatService.chat.data.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
public class ChatRoomDto {
    private String roomNumber;

    public ChatRoomDto(String roomNumber) {
        this.roomNumber = roomNumber;
    }
}
