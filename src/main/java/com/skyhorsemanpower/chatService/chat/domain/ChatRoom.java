package com.skyhorsemanpower.chatService.chat.domain;

import com.skyhorsemanpower.chatService.common.CommonCreateTime;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
@Getter
public class ChatRoom extends CommonCreateTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private Set<ChatRoomMember> chatRoomMembers = new HashSet<>();
    private String roomNumber;

    @Builder
    public ChatRoom(Set<ChatRoomMember> chatRoomMembers, String roomNumber) {
        this.chatRoomMembers = chatRoomMembers;
        this.roomNumber = roomNumber;
    }
    public void addChatRoomMember(ChatRoomMember member) {
        this.chatRoomMembers.add(member);
    }
}
