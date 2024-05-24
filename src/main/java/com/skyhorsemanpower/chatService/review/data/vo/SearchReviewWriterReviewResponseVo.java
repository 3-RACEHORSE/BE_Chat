package com.skyhorsemanpower.chatService.review.data.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SearchReviewWriterReviewResponseVo {
    private String AuctionUuid;
    private Byte reviewRate;
    private String reviewContent;

    @Builder
    public SearchReviewWriterReviewResponseVo(String auctionUuid, Byte reviewRate,
        String reviewContent) {
        AuctionUuid = auctionUuid;
        this.reviewRate = reviewRate;
        this.reviewContent = reviewContent;
    }
}
