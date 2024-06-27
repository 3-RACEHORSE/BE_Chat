package com.skyhorsemanpower.chatService.chat.data.dto;

import com.skyhorsemanpower.chatService.chat.data.vo.ChatRoomMemberResponseVo;
import com.skyhorsemanpower.chatService.chat.data.vo.ChatRoomTitleResponseVo;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatRoomMemberResponseDto {
    String memberUuid;
    String profileImage;
    String handle;

    public static ChatRoomMemberResponseVo dtoToVo(ChatRoomMemberResponseDto chatRoomMemberResponseDto) {
        return ChatRoomMemberResponseVo.builder()
            .memberUuid(chatRoomMemberResponseDto.getMemberUuid())
            .handle(chatRoomMemberResponseDto.getHandle())
            .profileImage(chatRoomMemberResponseDto.getProfileImage())
        .build();
    }
}
