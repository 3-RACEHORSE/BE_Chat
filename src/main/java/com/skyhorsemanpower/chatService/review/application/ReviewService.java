package com.skyhorsemanpower.chatService.review.application;

import com.skyhorsemanpower.chatService.review.data.dto.CreateReviewDto;

public interface ReviewService {
    void createReview(CreateReviewDto createReviewDto);
}