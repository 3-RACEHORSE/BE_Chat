package com.skyhorsemanpower.chatService.chat.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Entity
@Getter
public class ChatRoomMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private ChatRoom chatRoom;
    private String memberUuid;
    private String memberHandle;
    private String memberProfileImage;
    @Builder
    public ChatRoomMember(ChatRoom chatRoom, String memberUuid, String memberHandle,
        String memberProfileImage) {
        this.chatRoom = chatRoom;
        this.memberUuid = memberUuid;
        this.memberHandle = memberHandle;
        this.memberProfileImage = memberProfileImage;
    }
}
