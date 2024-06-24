package com.skyhorsemanpower.chatService.review.presentation;

import com.skyhorsemanpower.chatService.chat.data.vo.AuctionInfoResponseVo;
import com.skyhorsemanpower.chatService.common.AuctionPostClient;
import com.skyhorsemanpower.chatService.common.SuccessResponse;
import com.skyhorsemanpower.chatService.review.application.ReviewService;
import com.skyhorsemanpower.chatService.review.data.dto.CreateReviewDto;
import com.skyhorsemanpower.chatService.review.data.dto.ReviewResponseDto;
import com.skyhorsemanpower.chatService.review.data.vo.CreateReviewRequestVo;
import com.skyhorsemanpower.chatService.review.data.vo.ReviewResponseVo;
import com.skyhorsemanpower.chatService.review.data.vo.SearchReviewWriterReviewResponseVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/review")
@Tag(name = "리뷰 서비스", description = "리뷰 관련 API")
@Slf4j
public class ReviewController {

    private final ReviewService reviewService;
    private final AuctionPostClient auctionPostClient;

    @PostMapping
    @Operation(summary = "리뷰 생성", description = "모든 과정이 끝난 후 사용자가 리뷰를 남긴다")
    public SuccessResponse<Object> createReview(
        @RequestHeader String uuid, @RequestHeader String authorization,
        @RequestBody CreateReviewRequestVo createReviewRequestVo) {
        reviewService.createReview(
            CreateReviewDto.createReviewVoToDto(uuid, createReviewRequestVo), authorization);
        return new SuccessResponse<>(null);
    }

    @GetMapping(value = "/review_writer")
    @Operation(summary = "로그인된 회원의 리뷰 조회", description = "로그인된 회원의 모든 리뷰를 확인")
    public SuccessResponse<List<ReviewResponseVo>> searchReviewWriterReview(
        @RequestHeader String uuid) {
        List<ReviewResponseDto> reviewResponseDtos = reviewService.searchReviewWriterReview(
            uuid);
        return new SuccessResponse<>(reviewResponseDtos.stream()
            .map(ReviewResponseDto::dtoToVo).toList());
    }

    @GetMapping(value = "/influencer/{influencer_uuid}")
    @Operation(summary = "인플루언서의 리뷰 조회", description = "인플루언서의 리뷰를 확인")
    public SuccessResponse<List<ReviewResponseVo>> searchInfluencerReview(
        @PathVariable(value = "influencer_uuid") String influencer_uuid, @RequestHeader String uuid) {
        List<ReviewResponseDto> reviewResponseDtos = reviewService.searchInfluencerReview(
            influencer_uuid);
        return new SuccessResponse<>(reviewResponseDtos.stream()
            .map(ReviewResponseDto::dtoToVo).toList());
    }

    @GetMapping(value = "/test/{auction_uuid}")
    public SuccessResponse<AuctionInfoResponseVo> test(
        @PathVariable(value = "auction_uuid") String auctionUuid,
        @RequestHeader String uuid, @RequestHeader String authorization) {
        AuctionInfoResponseVo auctionInfoResponseVo = auctionPostClient.getAuctionInfo(auctionUuid, uuid, authorization);
        return new SuccessResponse<>(auctionInfoResponseVo);
    }
}
