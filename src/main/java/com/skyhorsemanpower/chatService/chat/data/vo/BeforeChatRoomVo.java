package com.skyhorsemanpower.chatService.chat.data.vo;

import com.skyhorsemanpower.chatService.chat.data.dto.BeforeChatRoomDto;
import com.skyhorsemanpower.chatService.chat.data.dto.LeaveChatRoomDto;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@Getter
@ToString
public class BeforeChatRoomVo {
    private String auctionUuid;
    private List<String> memberUuids;
    @Builder
    public BeforeChatRoomVo(String auctionUuid, List<String> memberUuids) {
        this.auctionUuid = auctionUuid;
        this.memberUuids = memberUuids;
    }

    public BeforeChatRoomDto toBeforeChatRoomDto() {
        return BeforeChatRoomDto.builder()
            .auctionUuid(this.auctionUuid)
            .memberUuids(this.memberUuids)
            .build();
    }
}
