package com.skyhorsemanpower.chatService.review.application;

import com.skyhorsemanpower.chatService.common.CustomException;
import com.skyhorsemanpower.chatService.common.ResponseStatus;
import com.skyhorsemanpower.chatService.review.data.dto.CreateReviewDto;
import com.skyhorsemanpower.chatService.review.data.vo.SearchAuctionReviewResponseVo;
import com.skyhorsemanpower.chatService.review.data.vo.SearchReviewWriterReviewResponseVo;
import com.skyhorsemanpower.chatService.review.domain.Review;
import com.skyhorsemanpower.chatService.review.infrastructure.ReviewRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

    @Override
    public SearchAuctionReviewResponseVo searchAuctionReview(String auctionUuid) {
        // Todo 판매자 정보에서 조회하는 것이라 판매자의 uuid로 경매 uuid를 조회하는 것을 실행하고 리뷰 담기
        Optional<Review> optionalReview = reviewRepository.findByAuctionUuid(auctionUuid);
        if(optionalReview.isPresent()) {
            return SearchAuctionReviewResponseVo.builder()
                //Todo 나중에 uuid로 핸들을 조회해서 builder안에 uuid대신 handle과 프로필 사진을 담는 과정 추가
                .reviewWriterUuid(optionalReview.get().getReviewWriterUuid())
                //현재는 리뷰작성자 uuid를 담지만 수정 예정
                .reviewContent(optionalReview.get().getReviewContent())
                .reviewRate(optionalReview.get().getReviewRate())
                .build();
        } else {
            throw new CustomException(ResponseStatus.WRONG_REQUEST);
        }
    }

    @Override
    public List<SearchReviewWriterReviewResponseVo> searchReviewWriterReview(String reviewWriterUuid) {
        List<Review> reviews = reviewRepository.findAllByReviewWriterUuid(reviewWriterUuid);
        List<SearchReviewWriterReviewResponseVo> searchReviewWriterReviewResponseVos = new ArrayList<>();
        if (!reviews.isEmpty()){
            for (Review review : reviews) {
                SearchReviewWriterReviewResponseVo responseVo = SearchReviewWriterReviewResponseVo.builder()
                    //Todo 현재 경매 uuid로 조회할수 없는데 경매uuid로 조회해서 제목과 필요한 값들을 넣기
                    .auctionUuid(review.getAuctionUuid())
                    .reviewContent(review.getReviewContent())
                    .reviewRate(review.getReviewRate())
                    .build();
                searchReviewWriterReviewResponseVos.add(responseVo);
            }
        } else {
            throw new CustomException(ResponseStatus.NO_DATA);
        }

        return searchReviewWriterReviewResponseVos;
    }
}