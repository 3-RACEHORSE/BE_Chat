package com.skyhorsemanpower.chatService.review.data.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SearchReviewResponseVo {
    private String reviewWriterUuid;
    private Byte reviewRate;
    private String reviewContent;
}
