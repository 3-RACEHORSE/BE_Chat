package com.skyhorsemanpower.chatService.chat.data.dto;

import com.skyhorsemanpower.chatService.chat.domain.ChatRoom;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
public class ChatRoomListElementDto {
    private String roomNumber;
    private String lastChat;
    private LocalDateTime lastChatTime;
    private String memberUuid;
    private String handle;
    private String profileImage;
}