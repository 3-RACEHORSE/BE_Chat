package com.skyhorsemanpower.chatService.chat.domain;

import com.skyhorsemanpower.chatService.common.CommonCreateTime;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
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

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "chat_room_members", joinColumns = @JoinColumn(name = "chat_room_id"))
    @Column(name = "member_uuid")
    private Set<String> memberUuids;

    private String roomNumber;
    private String lastChat;
    private LocalDateTime lastChatTime;

    @Builder
    public ChatRoom(Long id, Set<String> memberUuids, String roomNumber, String lastChat, LocalDateTime lastChatTime) {
        this.id = id;
        this.memberUuids = memberUuids;
        this.roomNumber = roomNumber;
        this.lastChat = lastChat;
        this.lastChatTime = lastChatTime;
    }

    public void updateLastChat(String lastChat, LocalDateTime lastChatTime) {
        this.lastChat = lastChat;
        this.lastChatTime = lastChatTime;
    }
}
