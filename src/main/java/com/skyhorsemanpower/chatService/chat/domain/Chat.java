package com.skyhorsemanpower.chatService.chat.domain;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Document(collection = "chat")
public class Chat {

    @Id
    private String Id;
    private String senderUuid;
    private String content;
    private String imageUrl;
    private String roomNumber;
    private LocalDateTime createdAt;

    @Builder
    public Chat(String senderUuid, String content, String imageUrl, String roomNumber,
        LocalDateTime createdAt) {
        this.senderUuid = senderUuid;
        this.content = content;
        this.imageUrl = imageUrl;
        this.roomNumber = roomNumber;
        this.createdAt = createdAt;
    }
}
