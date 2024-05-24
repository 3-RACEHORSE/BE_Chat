package com.skyhorsemanpower.chatService.review.application;

import com.skyhorsemanpower.chatService.review.data.dto.CreateReviewDto;
import com.skyhorsemanpower.chatService.review.data.vo.SearchAuctionReviewResponseVo;
import com.skyhorsemanpower.chatService.review.data.vo.SearchReviewResponseVo;
import com.skyhorsemanpower.chatService.review.data.vo.SearchReviewWriterReviewResponseVo;
import java.util.List;
import reactor.core.publisher.Flux;

public interface ReviewService {
    void createReview(CreateReviewDto createReviewDto);

    SearchAuctionReviewResponseVo searchAuctionReview(String auctionUuid);

    List<SearchReviewWriterReviewResponseVo> searchReviewWriterReview(String reviewWriterUuid);
}