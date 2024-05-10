package com.skyhorsemanpower.chatService.chat.data.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
public class ChatRoomDto {
    private String memberUuid1;
    private String memberUuid2;

    public ChatRoomDto(String memberUuid1, String memberUuid2) {
        this.memberUuid1 = memberUuid1;
        this.memberUuid2 = memberUuid2;
    }
}
