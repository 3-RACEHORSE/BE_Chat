package com.skyhorsemanpower.chatService.review.data.vo;

import lombok.Getter;

@Getter
public class CreateReviewRequestVo {
    private String auctionUuid;
    private int reviewRate;
    private String reviewContent;
}
