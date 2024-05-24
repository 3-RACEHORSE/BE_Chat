package com.skyhorsemanpower.chatService.review.data.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SearchAuctionReviewResponseVo {
    private String reviewWriterUuid;
    private Byte reviewRate;
    private String reviewContent;

    @Builder
    public SearchAuctionReviewResponseVo(String reviewWriterUuid, Byte reviewRate,
        String reviewContent) {
        this.reviewWriterUuid = reviewWriterUuid;
        this.reviewRate = reviewRate;
        this.reviewContent = reviewContent;
    }
}
