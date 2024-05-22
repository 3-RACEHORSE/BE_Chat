package com.skyhorsemanpower.chatService.review.data.dto;

import com.skyhorsemanpower.chatService.review.data.vo.CreateReviewRequestVo;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
//@Setter
@NoArgsConstructor
public class CreateReviewDto {
    private String reviewWriterUuid;
    private String auctionUuid;
    private Byte reviewRate;
    private String reviewContent;

    @Builder
    public CreateReviewDto(String reviewWriterUuid, String auctionUuid, Byte reviewRate,
        String reviewContent) {
        this.reviewWriterUuid = reviewWriterUuid;
        this.auctionUuid = auctionUuid;
        this.reviewRate = reviewRate;
        this.reviewContent = reviewContent;
    }

    public static CreateReviewDto createReviewVoToDto(String uuid, CreateReviewRequestVo createReviewRequestVo) {
        return CreateReviewDto.builder()
            .reviewWriterUuid(uuid)
            .auctionUuid(createReviewRequestVo.getAuctionUuid())
            .reviewRate(createReviewRequestVo.getReviewRate())
            .reviewContent(createReviewRequestVo.getReviewContent())
        .build();
    }
}
