package com.skyhorsemanpower.chatService.chat.data.dto;

import com.skyhorsemanpower.chatService.chat.domain.ChatRoom;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ChatRoomListElementDto {
    private Long id;
    private String roomNumber;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
    private String memberUuid;

    public static ChatRoomListElementDto fromEntityAndOtherUserUuid(ChatRoom chatRoom, String otherUserUuid) {
        return ChatRoomListElementDto.builder()
            .id(chatRoom.getId())
            .roomNumber(chatRoom.getRoomNumber())
            .lastMessage(chatRoom.getLastMessage())
            .lastMessageTime(chatRoom.getLastMessageTime())
            .memberUuid(otherUserUuid) // 상대방 프로필을 띄우려고 일단 uuid 다 들고 옴
            .build();
    }
}