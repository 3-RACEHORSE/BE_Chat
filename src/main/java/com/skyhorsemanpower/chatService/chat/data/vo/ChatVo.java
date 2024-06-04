package com.skyhorsemanpower.chatService.chat.data.vo;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class ChatVo {
    private String senderUuid;
    private String content;
    private String roomNumber;
    private LocalDateTime createdAt;
    private int readCount;
    private String handle;
    private String profileImage;

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setReadCount(int readCount) {
        this.readCount = readCount;
    }

    public void setSenderUuid(String senderUuid) {
        this.senderUuid = senderUuid;
    }

}
