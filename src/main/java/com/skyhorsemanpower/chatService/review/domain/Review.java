package com.skyhorsemanpower.chatService.review.domain;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Document(collection = "review")
public class Review {
    @Id
    private String id;
    private String reviewWriterUuid;
    private String auctionUuid;
    private Byte reviewRate;
    private String reviewContent;
    private String reviewImage;

    @Builder
    public Review(String reviewWriterUuid, String auctionUuid, Byte reviewRate,
        String reviewContent,
        String reviewImage) {
        this.reviewWriterUuid = reviewWriterUuid;
        this.auctionUuid = auctionUuid;
        this.reviewRate = reviewRate;
        this.reviewContent = reviewContent;
        this.reviewImage = reviewImage;
    }
}
