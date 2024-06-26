package com.skyhorsemanpower.chatService.chat.data.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateProfileImageRequestDto {
    private String profileImage;
    private String memberUuid;
}
