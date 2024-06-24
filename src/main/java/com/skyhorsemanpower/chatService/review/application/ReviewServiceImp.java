package com.skyhorsemanpower.chatService.review.application;

import com.skyhorsemanpower.chatService.chat.data.vo.AuctionInfoResponseVo;
import com.skyhorsemanpower.chatService.common.AuctionPostClient;
import com.skyhorsemanpower.chatService.common.MemberPostClient;
import com.skyhorsemanpower.chatService.common.response.CustomException;
import com.skyhorsemanpower.chatService.common.response.ResponseStatus;
import com.skyhorsemanpower.chatService.review.data.dto.CreateReviewDto;
import com.skyhorsemanpower.chatService.review.data.dto.ReviewResponseDto;
import com.skyhorsemanpower.chatService.review.data.dto.MemberInfoResponseDto;
import com.skyhorsemanpower.chatService.review.domain.Review;
import com.skyhorsemanpower.chatService.review.infrastructure.ReviewRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewServiceImp implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final AuctionPostClient auctionPostClient;
    private final MemberPostClient memberPostClient;

    @Override
    public void createReview(CreateReviewDto createReviewDto, String authorization) {
        try {
            AuctionInfoResponseVo auctionInfoResponseVo = auctionPostClient
                .getAuctionInfo(createReviewDto.getAuctionUuid(),
                    createReviewDto.getReviewWriterUuid(), authorization);
            MemberInfoResponseDto memberInfoResponseDto = memberPostClient.getMemberInfo(
                createReviewDto.getReviewWriterUuid(), authorization);
            // Todo 해당 회원이 경매 낙찰자인지 검증하는게 필요
            Review review = Review.builder()
                .reviewWriterUuid(createReviewDto.getReviewWriterUuid())
                .reviewWriterName(memberInfoResponseDto.getName())
                .influencerUuid(auctionInfoResponseVo.getInfluencerUuid())
                .influencerName(auctionInfoResponseVo.getInfluencerName())
                .auctionUuid(createReviewDto.getAuctionUuid())
                .reviewContent(createReviewDto.getReviewContent())
                .reviewRate(createReviewDto.getReviewRate())
                .build();
            reviewRepository.save(review);
        } catch (Exception e) {
            throw new CustomException(ResponseStatus.SAVE_REVIEW_FAILED);
        }
    }

    @Override
    public List<ReviewResponseDto> searchInfluencerReview(String influencerUuid) {
        try {
            List<Review> reviews = reviewRepository.findAllByInfluencerUuid(influencerUuid);

            return reviews.stream()
                .map(review -> ReviewResponseDto.builder()
                    .reviewWriterUuid(review.getReviewWriterUuid())
                    .reviewWriterName(review.getReviewWriterName())
                    .influencerUuid(review.getInfluencerUuid())
                    .influencerName(review.getInfluencerName())
                    .reviewContent(review.getReviewContent())
                    .reviewRate(review.getReviewRate())
                    .build())
                .toList();
        } catch (Exception e) {
            throw new CustomException(ResponseStatus.MONGO_DB_ERROR);
        }
    }

    @Override
    public List<ReviewResponseDto> searchReviewWriterReview(String reviewWriterUuid) {
        try {
            List<Review> reviews = reviewRepository.findAllByReviewWriterUuid(reviewWriterUuid);
            return reviews.stream()
                .map(review -> ReviewResponseDto.builder()
                    .reviewWriterUuid(review.getReviewWriterUuid())
                    .reviewWriterName(review.getReviewWriterName())
                    .influencerUuid(review.getInfluencerUuid())
                    .influencerName(review.getInfluencerName())
                    .reviewContent(review.getReviewContent())
                    .reviewRate(review.getReviewRate())
                    .build())
                .toList();
        } catch (Exception e) {
            throw new CustomException(ResponseStatus.MONGO_DB_ERROR);
        }
    }
}