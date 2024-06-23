package com.skyhorsemanpower.chatService.chat.data.dto;

import com.skyhorsemanpower.chatService.chat.data.vo.ChatRoomTitleResponseVo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomTitleResponseDto {
    private String title;
    public static ChatRoomTitleResponseVo dtoToVo(ChatRoomTitleResponseDto chatRoomTitleResponseDto) {
        return new ChatRoomTitleResponseVo(
            chatRoomTitleResponseDto.getTitle()
        );
    }
}
