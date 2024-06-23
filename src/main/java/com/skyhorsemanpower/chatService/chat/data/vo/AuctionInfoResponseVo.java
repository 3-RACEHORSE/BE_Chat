package com.skyhorsemanpower.chatService.chat.data.vo;

import com.skyhorsemanpower.chatService.chat.data.dto.ExtractAuctionInformationWithMemberUuidsDto;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AuctionInfoResponseVo {

    private String auctionPostId;
    private String auctionUuid;
    private String adminUuid;
    private String influencerUuid;
    private String influencerName;
    private String title;
    private String content;
    private int numberOfEventParticipants;
    private String localName;
    private String eventPlace;
    private LocalDateTime eventStartTime;
    private LocalDateTime eventCloseTime;
    private LocalDateTime auctionStartTime;
    private LocalDateTime auctionEndTime;
    private int startPrice;
    private int incrementUnit;
    private int totalDonation;
    private String state;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String thumbnail;
    private List<String> images;

    public ExtractAuctionInformationWithMemberUuidsDto toExtractAuctionInformationWithMemberUuidsDto() {
        return ExtractAuctionInformationWithMemberUuidsDto.builder()
            .adminUuid(this.adminUuid)
            .title(this.title)
            .thumbnail(this.thumbnail)
            .build();
    }
}
