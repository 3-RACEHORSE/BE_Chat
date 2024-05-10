package com.skyhorsemanpower.chatService.chat.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
@Getter
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String chatMember1;

    private String chatMember2;

    private LocalDateTime createdAt; // 생성시간

    private String lastChat; // 마지막 채팅

    private LocalDateTime lastChatCreatedAt; // 마지막 채팅 전송시간
    @Builder

    public ChatRoom(Long id, String chatMember1, String chatMember2, LocalDateTime createdAt,
        String lastChat, LocalDateTime lastChatCreatedAt) {
        this.id = id;
        this.chatMember1 = chatMember1;
        this.chatMember2 = chatMember2;
        this.createdAt = createdAt;
        this.lastChat = lastChat;
        this.lastChatCreatedAt = lastChatCreatedAt;
    }
}
