package com.skyhorsemanpower.chatService.review.application;

import com.skyhorsemanpower.chatService.review.data.dto.CreateReviewDto;
import com.skyhorsemanpower.chatService.review.data.dto.ReviewResponseDto;
import com.skyhorsemanpower.chatService.review.data.vo.ReviewResponseVo;
import com.skyhorsemanpower.chatService.review.data.vo.SearchReviewWriterReviewResponseVo;
import java.util.List;

public interface ReviewService {
    void createReview(CreateReviewDto createReviewDto, String authorization);

    List<ReviewResponseDto> searchInfluencerReview(String influencerUuid);

    List<ReviewResponseDto> searchReviewWriterReview(String reviewWriterUuid);
}