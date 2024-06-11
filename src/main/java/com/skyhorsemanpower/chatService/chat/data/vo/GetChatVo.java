package com.skyhorsemanpower.chatService.chat.data.vo;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class GetChatVo {
    private String content;
    private LocalDateTime createdAt;
    private String handle;
    private String profileImage;

    @Builder
    public GetChatVo(String content, LocalDateTime createdAt, String handle,
        String profileImage) {
        this.content = content;
        this.createdAt = createdAt;
        this.handle = handle;
        this.profileImage = profileImage;
    }
}
