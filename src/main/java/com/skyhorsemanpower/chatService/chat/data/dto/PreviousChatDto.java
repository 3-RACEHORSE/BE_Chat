package com.skyhorsemanpower.chatService.chat.data.dto;

import java.time.LocalDateTime;
import lombok.Getter;
@Getter
public class PreviousChatDto {
    private String senderUuid;
    private String content;
    private LocalDateTime createdAt;
    private int readCount;
}
