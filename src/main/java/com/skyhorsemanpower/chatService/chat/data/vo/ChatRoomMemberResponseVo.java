package com.skyhorsemanpower.chatService.chat.data.vo;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatRoomMemberResponseVo {
    String memberUuid;
    String profileImage;
    String handle;
}
