package com.skyhorsemanpower.chatService.chat.data.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skyhorsemanpower.chatService.common.JsonPropertyEnum;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
@Getter
public class PreviousChatWithMemberInfoDto {
    private String uuid;
    private String handle;
    private String profileImage;
    private String content;
    private LocalDateTime createdAt;

    @Builder
    public PreviousChatWithMemberInfoDto(String uuid, String handle, String profileImage,
        String content, LocalDateTime createdAt) {
        this.uuid = uuid;
        this.handle = handle;
        this.profileImage = profileImage;
        this.content = content;
        this.createdAt = createdAt;
    }
}
