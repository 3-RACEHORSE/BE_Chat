package com.skyhorsemanpower.chatService.chat.domain;

import com.skyhorsemanpower.chatService.common.CommonCreateTime;
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
    private int readCount;

    @Builder
    public Chat(String senderUuid, String content, String imageUrl, String roomNumber,
        LocalDateTime createdAt, int readCount) {
        this.senderUuid = senderUuid;
        this.content = content;
        this.imageUrl = imageUrl;
        this.roomNumber = roomNumber;
        this.createdAt = createdAt;
        this.readCount = readCount;
    }
}
