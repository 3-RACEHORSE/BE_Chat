package com.skyhorsemanpower.chatService.chat.data.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
public class ChatMemberDto {
    private String firstMemberUuid;
    private String secondMemberUuid;

    public ChatMemberDto(String firstMemberUuid, String secondMemberUuid) {
        this.firstMemberUuid = firstMemberUuid;
        this.secondMemberUuid = secondMemberUuid;
    }
}
