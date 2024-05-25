package com.skyhorsemanpower.chatService.review.data.vo;

import lombok.Getter;

@Getter
public class CreateReviewRequestVo {
    private String auctionUuid;
    private Byte reviewRate;
    private String reviewContent;
}
