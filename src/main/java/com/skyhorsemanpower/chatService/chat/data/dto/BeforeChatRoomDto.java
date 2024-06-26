package com.skyhorsemanpower.chatService.chat.data.dto;

import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BeforeChatRoomDto {

    private String auctionUuid;
    private Map<String, String> memberUuidsWithProfiles;
    private String title;
    private String thumbnail;
    private String adminUuid;
}
