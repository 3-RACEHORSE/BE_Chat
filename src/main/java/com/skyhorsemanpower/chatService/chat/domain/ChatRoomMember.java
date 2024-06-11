package com.skyhorsemanpower.chatService.chat.domain;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Document(collection = "chatRoomMember")
public class ChatRoomMember {

    private String memberUuid;
    private String memberHandle;
    private String memberProfileImage;
    @Builder
    public ChatRoomMember(String memberUuid, String memberHandle,
        String memberProfileImage) {
        this.memberUuid = memberUuid;
        this.memberHandle = memberHandle;
        this.memberProfileImage = memberProfileImage;
    }
}
