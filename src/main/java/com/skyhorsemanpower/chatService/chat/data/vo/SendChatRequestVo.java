package com.skyhorsemanpower.chatService.chat.data.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.skyhorsemanpower.chatService.chat.data.dto.LeaveChatRoomDto;
import com.skyhorsemanpower.chatService.chat.data.dto.SendChatRequestDto;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SendChatRequestVo {
    private String content;
    private String roomNumber;

    public SendChatRequestDto toSendChatRequestDto() {
        return SendChatRequestDto.builder()
            .content(this.content)
            .roomNumber(this.roomNumber)
            .build();
    }
}
