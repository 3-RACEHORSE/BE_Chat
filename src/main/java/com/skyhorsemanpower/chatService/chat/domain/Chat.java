package com.skyhorsemanpower.chatService.chat.domain;

import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "chat")
public class Chat {

    @Id
    private String Id;
    private String senderUuid;
    private String content;
    private String imageUrl;
    private String roomNumber;

    @Builder
    public Chat(String senderUuid, String content, String imageUrl, String roomNumber) {
        this.senderUuid = senderUuid;
        this.content = content;
        this.imageUrl = imageUrl;
        this.roomNumber = roomNumber;
    }
}
