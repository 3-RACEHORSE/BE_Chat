package com.skyhorsemanpower.chatService.chat.data.vo;

import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class ChatRoomWithLastChatVo {
    private String memberUuid;
    private String roomNumber;
    private String content;
    private LocalDateTime createdAt;
}
