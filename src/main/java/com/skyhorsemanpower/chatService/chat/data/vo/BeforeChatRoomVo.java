package com.skyhorsemanpower.chatService.chat.data.vo;

import com.skyhorsemanpower.chatService.chat.data.dto.BeforeChatRoomDto;
import com.skyhorsemanpower.chatService.chat.data.dto.LeaveChatRoomDto;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@Builder
public class BeforeChatRoomVo {
    private String auctionUuid;
    private String title;
    private List<String> memberUuids;
    private String thumbnail;
    private String adminUuid;

    public BeforeChatRoomDto toBeforeChatRoomDto() {
        return BeforeChatRoomDto.builder()
            .auctionUuid(this.auctionUuid)
            .title(this.title)
            .thumbnail(this.thumbnail)
            .memberUuids(this.memberUuids)
            .adminUuid(this.adminUuid)
            .build();
    }
}
