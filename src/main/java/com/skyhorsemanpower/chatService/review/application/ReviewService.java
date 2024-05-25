package com.skyhorsemanpower.chatService.review.application;

import com.skyhorsemanpower.chatService.review.data.dto.CreateReviewDto;
import com.skyhorsemanpower.chatService.review.data.vo.SearchAuctionReviewResponseVo;
import com.skyhorsemanpower.chatService.review.data.vo.SearchReviewWriterReviewResponseVo;
import java.util.List;

public interface ReviewService {
    void createReview(CreateReviewDto createReviewDto);

    SearchAuctionReviewResponseVo searchAuctionReview(String auctionUuid);

    List<SearchReviewWriterReviewResponseVo> searchReviewWriterReview(String reviewWriterUuid);
}