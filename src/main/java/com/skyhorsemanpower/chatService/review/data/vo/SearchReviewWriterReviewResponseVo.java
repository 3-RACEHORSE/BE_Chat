package com.skyhorsemanpower.chatService.review.data.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SearchReviewWriterReviewResponseVo {
    private String auctionUuid;
    private int reviewRate;
    private String reviewContent;
    // 경매 uuid로 조회해서 경매 제목이나 사진 같은것 담기로 수정해야할듯
    @Builder
    public SearchReviewWriterReviewResponseVo(String auctionUuid, int reviewRate,
        String reviewContent) {
        this.auctionUuid = auctionUuid;
        this.reviewRate = reviewRate;
        this.reviewContent = reviewContent;
    }
}
