package com.skyhorsemanpower.chatService.review.domain;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Builder
@Document(collection = "review")
public class Review {
    @Id
    private String id;
    private String reviewWriterUuid;
    private String reviewWriterName;
    private String influencerUuid;
    private String influencerName;
    private String auctionUuid;
    private int reviewRate;
    private String reviewContent;



}
