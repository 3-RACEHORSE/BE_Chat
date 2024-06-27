package com.skyhorsemanpower.chatService.chat.data.dto;

import com.skyhorsemanpower.chatService.chat.data.vo.UnReadChatCountResponseVo;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UnReadChatCountResponseDto {
    Long count;

    public static UnReadChatCountResponseVo dtoToVo(UnReadChatCountResponseDto unReadChatCountResponseDto) {
        return UnReadChatCountResponseVo.builder()
            .count(unReadChatCountResponseDto.getCount())
            .build();
    }
}
