package com.skyhorsemanpower.chatService.review.data.vo;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReviewResponseVo {

    private String reviewWriterName;
    private String influencerName;
    private int reviewRate;
    private String reviewContent;
}
