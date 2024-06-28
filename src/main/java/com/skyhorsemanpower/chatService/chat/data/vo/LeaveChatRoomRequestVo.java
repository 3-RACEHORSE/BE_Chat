package com.skyhorsemanpower.chatService.chat.data.vo;

import com.skyhorsemanpower.chatService.chat.data.dto.LeaveChatRoomDto;
import lombok.Getter;

@Getter
public class LeaveChatRoomRequestVo {
    private String roomNumber;
    private String uuid;

    public LeaveChatRoomRequestVo(String roomNumber, String uuid) {
        this.roomNumber = roomNumber;
        this.uuid = uuid;
    }

    public LeaveChatRoomDto toLeaveChatRoomDto() {
        return LeaveChatRoomDto.builder()
            .roomNumber(this.roomNumber)
            .uuid(this.uuid)
            .build();
    }

}
