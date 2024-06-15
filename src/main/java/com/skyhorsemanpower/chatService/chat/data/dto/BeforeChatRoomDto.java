package com.skyhorsemanpower.chatService.chat.data.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BeforeChatRoomDto {
    private String auctionUuid;
    private List<String> memberUuids;
}
