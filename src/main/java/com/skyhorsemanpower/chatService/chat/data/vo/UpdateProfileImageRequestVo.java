package com.skyhorsemanpower.chatService.chat.data.vo;

import com.skyhorsemanpower.chatService.chat.data.dto.UpdateProfileImageRequestDto;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateProfileImageRequestVo {

    private String profileImage;
    private String memberUuid;

    public UpdateProfileImageRequestDto toUpdateProfileImageRequestDto() {
        return UpdateProfileImageRequestDto.builder()
            .memberUuid(this.memberUuid)
            .profileImage(this.profileImage)
            .build();
    }
}