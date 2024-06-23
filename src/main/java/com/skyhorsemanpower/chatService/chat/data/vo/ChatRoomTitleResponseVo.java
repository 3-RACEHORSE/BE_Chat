package com.skyhorsemanpower.chatService.chat.data.vo;

import com.skyhorsemanpower.chatService.chat.data.dto.ChatRoomTitleResponseDto;
import lombok.Getter;

@Getter
public class ChatRoomTitleResponseVo {
    private String title;

    public ChatRoomTitleResponseVo(String title) {
        this.title = title;
    }
}
