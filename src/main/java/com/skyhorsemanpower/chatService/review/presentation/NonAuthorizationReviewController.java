package com.skyhorsemanpower.chatService.review.presentation;

import com.skyhorsemanpower.chatService.common.SuccessResponse;
import com.skyhorsemanpower.chatService.review.application.ReviewService;
import com.skyhorsemanpower.chatService.review.data.vo.SearchAuctionReviewResponseVo;
import com.skyhorsemanpower.chatService.review.data.vo.SearchReviewResponseVo;
import com.skyhorsemanpower.chatService.review.domain.Review;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/non-authorization/review")
@Tag(name = "인가가 필요없는 리뷰", description = "인가가 필요없는 리뷰 관련 API")
@Slf4j
public class NonAuthorizationReviewController {
    private final ReviewService reviewService;
    @GetMapping(value = "/auction_review/{auction_uuid}")
    @Operation(summary = "리뷰 조회", description = "판매자의 모든 리뷰를 확인")
    public SuccessResponse<SearchAuctionReviewResponseVo> searchAuctionReview(
        @PathVariable(value = "auction_uuid") String auction_uuid) {
        SearchAuctionReviewResponseVo searchAuctionReviewResponseVo = reviewService.searchAuctionReview(auction_uuid);
        return new SuccessResponse<>(searchAuctionReviewResponseVo);
    }
}