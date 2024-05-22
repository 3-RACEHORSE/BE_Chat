package com.skyhorsemanpower.chatService.review.application;

import com.skyhorsemanpower.chatService.common.CustomException;
import com.skyhorsemanpower.chatService.common.ResponseStatus;
import com.skyhorsemanpower.chatService.review.data.dto.CreateReviewDto;
import com.skyhorsemanpower.chatService.review.domain.Review;
import com.skyhorsemanpower.chatService.review.infrastructure.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewServiceImp implements ReviewService{
    private final ReviewRepository reviewRepository;
    @Override
    public void createReview(CreateReviewDto createReviewDto) {
        try {

            Review review = Review.builder()
                .reviewWriterUuid(createReviewDto.getReviewWriterUuid())
                .auctionUuid(createReviewDto.getAuctionUuid())
                .reviewContent(createReviewDto.getReviewContent())
                .reviewRate(createReviewDto.getReviewRate())
                .build();
            reviewRepository.save(review);
        } catch (Exception e) {
            throw new CustomException(ResponseStatus.SAVE_REVIEW_FAILED);
        }
    }
}