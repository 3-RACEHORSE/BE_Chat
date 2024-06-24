package com.skyhorsemanpower.chatService.review.data.dto;

import com.skyhorsemanpower.chatService.review.data.vo.ReviewResponseVo;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ReviewResponseDto {

    private String reviewWriterUuid;
    private String reviewWriterName;
    private String influencerUuid;
    private String influencerName;
    private int reviewRate;
    private String reviewContent;

    public static ReviewResponseVo dtoToVo(ReviewResponseDto reviewResponseDto) {
        return ReviewResponseVo.builder()
            .reviewWriterName(reviewResponseDto.getReviewWriterName())
            .influencerName(reviewResponseDto.getInfluencerName())
            .reviewRate(reviewResponseDto.getReviewRate())
            .reviewContent(reviewResponseDto.getReviewContent())
            .build();
    }

}
