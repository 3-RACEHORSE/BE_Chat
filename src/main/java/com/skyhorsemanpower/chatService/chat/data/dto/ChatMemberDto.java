package com.skyhorsemanpower.chatService.chat.data.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
public class ChatMemberDto {
    private String memberUuid;

    public ChatMemberDto(String memberUuid) {
        this.memberUuid = memberUuid;
    }
}
