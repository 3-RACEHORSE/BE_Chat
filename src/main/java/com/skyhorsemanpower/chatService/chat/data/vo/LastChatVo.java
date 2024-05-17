package com.skyhorsemanpower.chatService.chat.data.vo;

import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class LastChatVo {
    private String content;
    private LocalDateTime createdAt;
}
