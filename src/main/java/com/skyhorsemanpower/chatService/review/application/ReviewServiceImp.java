package com.skyhorsemanpower.chatService.review.application;

import com.skyhorsemanpower.chatService.common.CustomException;
import com.skyhorsemanpower.chatService.common.ResponseStatus;
import com.skyhorsemanpower.chatService.review.data.dto.CreateReviewDto;
import com.skyhorsemanpower.chatService.review.data.vo.SearchAuctionReviewResponseVo;
import com.skyhorsemanpower.chatService.review.data.vo.SearchReviewWriterReviewResponseVo;
import com.skyhorsemanpower.chatService.review.domain.Review;
import com.skyhorsemanpower.chatService.review.infrastructure.ReviewAsyncRepository;
import com.skyhorsemanpower.chatService.review.infrastructure.ReviewSyncRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewServiceImp implements ReviewService{
    private final ReviewAsyncRepository reviewAsyncRepository;
    private final ReviewSyncRepository reviewSyncRepository;
    @Override
    public void createReview(CreateReviewDto createReviewDto) {
        try {
            Review review = Review.builder()
                .reviewWriterUuid(createReviewDto.getReviewWriterUuid())
                .auctionUuid(createReviewDto.getAuctionUuid())
                .reviewContent(createReviewDto.getReviewContent())
                .reviewRate(createReviewDto.getReviewRate())
                .build();
            reviewAsyncRepository.save(review).subscribe();
        } catch (Exception e) {
            throw new CustomException(ResponseStatus.SAVE_REVIEW_FAILED);
        }
    }

    @Override
    public SearchAuctionReviewResponseVo searchAuctionReview(String auctionUuid) {
        //Todo 나중에 핸들로 uuid조회하는 과정 추가
        Review review = reviewSyncRepository.findByAuctionUuid(auctionUuid);

        return SearchAuctionReviewResponseVo.builder()
            .reviewWriterUuid(review.getId())
            .reviewContent(review.getReviewContent())
            .reviewRate(review.getReviewRate())
            .build();
    }

    @Override
    public List<SearchReviewWriterReviewResponseVo> searchReviewWriterReview(String reviewWriterUuid) {
        List<Review> reviews = reviewSyncRepository.findAllByReviewWriterUuid(reviewWriterUuid);
        List<SearchReviewWriterReviewResponseVo> searchReviewWriterReviewResponseVos = new ArrayList<>();

        for (Review review : reviews) {
            SearchReviewWriterReviewResponseVo responseVo = SearchReviewWriterReviewResponseVo.builder()
                .auctionUuid(review.getAuctionUuid())
                .reviewContent(review.getReviewContent())
                .reviewRate(review.getReviewRate())
                .build();
            searchReviewWriterReviewResponseVos.add(responseVo);
        }

        return searchReviewWriterReviewResponseVos;
    }
}