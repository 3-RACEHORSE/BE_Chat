package com.skyhorsemanpower.chatService.chat.data.vo;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class ChatVo {
    private String senderUuid;
    private String content;
    private String roomNumber;
}
