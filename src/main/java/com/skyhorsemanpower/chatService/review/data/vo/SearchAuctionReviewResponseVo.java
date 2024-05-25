package com.skyhorsemanpower.chatService.review.data.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SearchAuctionReviewResponseVo {
    private String reviewWriterUuid;
    private int reviewRate;
    private String reviewContent;
    // 리뷰 작성자 uuid 담는 것은 타 api 활용하는거 확인 후 수정 예정
    @Builder
    public SearchAuctionReviewResponseVo(String reviewWriterUuid, int reviewRate,
        String reviewContent) {
        this.reviewWriterUuid = reviewWriterUuid;
        this.reviewRate = reviewRate;
        this.reviewContent = reviewContent;
    }
}
