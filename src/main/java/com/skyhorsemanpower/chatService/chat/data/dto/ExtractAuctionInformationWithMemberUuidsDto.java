package com.skyhorsemanpower.chatService.chat.data.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ExtractAuctionInformationWithMemberUuidsDto {
    private String adminUuid;
    private String title;
    private String thumbnail;
    private List<String> memberUuids;

    public void setMemberUuids(List<String> memberUuids) {
    }
}
