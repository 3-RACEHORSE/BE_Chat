package com.skyhorsemanpower.chatService.chat.domain;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Document(collection = "lastChat")
public class LastChat {
    @Id
    private String Id;
    private String roomNumber;
    private String content;
    private LocalDateTime lastChatTime;

    @Builder
    public LastChat(String roomNumber, String content, LocalDateTime lastChatTime) {
        this.roomNumber = roomNumber;
        this.content = content;
        this.lastChatTime = lastChatTime;
    }
}
