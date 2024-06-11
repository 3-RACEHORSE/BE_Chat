package com.skyhorsemanpower.chatService.chat.domain;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Getter
@Document(collection = "chatRoom")
public class ChatRoom {
    @Id
    private String id;
    private List<ChatRoomMember> chatRoomMembers;
    private String roomNumber;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String title;
    private String thumbnail;

    @Builder
    public ChatRoom(String id, List<ChatRoomMember> chatRoomMembers, String roomNumber,
        LocalDateTime createdAt, LocalDateTime updatedAt, String title, String thumbnail) {
        this.id = id;
        this.chatRoomMembers = chatRoomMembers;
        this.roomNumber = roomNumber;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.title = title;
        this.thumbnail = thumbnail;
    }
}
