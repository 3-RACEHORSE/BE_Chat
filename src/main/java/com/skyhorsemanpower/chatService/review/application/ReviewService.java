package com.skyhorsemanpower.chatService.review.application;

import com.skyhorsemanpower.chatService.review.data.dto.CreateReviewDto;
import com.skyhorsemanpower.chatService.review.data.vo.SearchReviewResponseVo;
import reactor.core.publisher.Flux;

public interface ReviewService {
    void createReview(CreateReviewDto createReviewDto);

    Flux<SearchReviewResponseVo> searchReview(String handle);
}