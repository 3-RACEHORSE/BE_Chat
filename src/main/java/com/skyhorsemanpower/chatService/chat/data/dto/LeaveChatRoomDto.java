package com.skyhorsemanpower.chatService.chat.data.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class LeaveChatRoomDto {
    private String roomNumber;
    private String uuid;

    @Builder
    public LeaveChatRoomDto(String roomNumber, String uuid) {
        this.roomNumber = roomNumber;
        this.uuid = uuid;
    }
}
