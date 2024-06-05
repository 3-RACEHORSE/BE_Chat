package com.skyhorsemanpower.chatService.chat.data.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skyhorsemanpower.chatService.common.JsonPropertyEnum;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
@Getter
public class PreviousChatWithMemberInfoDto {
    private String handle;
    private String profileImage;
    private String content;
    private LocalDateTime createdAt;
    private int readCount;

    @Builder
    public PreviousChatWithMemberInfoDto(String handle, String profileImage, String content,
        LocalDateTime createdAt, int readCount) {
        this.handle = handle;
        this.profileImage = profileImage;
        this.content = content;
        this.createdAt = createdAt;
        this.readCount = readCount;
    }
}
