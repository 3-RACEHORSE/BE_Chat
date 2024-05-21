package com.skyhorsemanpower.chatService.chat.data.dto;

import com.skyhorsemanpower.chatService.chat.domain.ChatRoom;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ChatRoomListDto {
    private Long id;
    private String roomNumber;
    private String lastChat;
    private LocalDateTime lastChatTime;
    private Set<String> memberUuids;

    public static ChatRoomListDto fromEntity(ChatRoom chatRoom) {
        return ChatRoomListDto.builder()
            .id(chatRoom.getId())
            .roomNumber(chatRoom.getRoomNumber())
            .lastChat(chatRoom.getLastChat())
            .lastChatTime(chatRoom.getLastChatTime())
            .memberUuids(chatRoom.getMemberUuids()) // 상대방 프로필을 띄우려고 일단 uuid 다 들고 옴
            .build();
    }

}