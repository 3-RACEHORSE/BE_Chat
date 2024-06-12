package com.skyhorsemanpower.chatService.chat.data.vo;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatRoomResponseVo {
    private String roomNumber;
    private LocalDateTime updatedAt;
    private String title;
    private String thumbnail;
}
