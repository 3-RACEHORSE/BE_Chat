package com.skyhorsemanpower.chatService.review.presentation;

import com.skyhorsemanpower.chatService.common.SuccessResponse;
import com.skyhorsemanpower.chatService.review.application.ReviewService;
import com.skyhorsemanpower.chatService.review.data.dto.CreateReviewDto;
import com.skyhorsemanpower.chatService.review.data.vo.CreateReviewRequestVo;
import com.skyhorsemanpower.chatService.review.data.vo.SearchAuctionReviewResponseVo;
import com.skyhorsemanpower.chatService.review.data.vo.SearchReviewWriterReviewResponseVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
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
@RequestMapping("/api/v1/authorization/review")
@Tag(name = "인가가 필요한 리뷰", description = "인가가 필요한 리뷰 관련 API")
@Slf4j
public class AuthorizationReviewController {
    private final ReviewService reviewService;

    @PostMapping
    @Operation(summary = "리뷰 생성", description = "모든 과정이 끝난 후 사용자가 리뷰를 남긴다")
    public SuccessResponse<Object> createReview(
        @RequestHeader String uuid,
        @RequestBody CreateReviewRequestVo createReviewRequestVo) {
        reviewService.createReview(CreateReviewDto.createReviewVoToDto(uuid, createReviewRequestVo));
        return new SuccessResponse<>(null);
    }
    @GetMapping(value = "/review_writer/{review_writer_uuid}")
    @Operation(summary = "리뷰 조회", description = "판매자의 모든 리뷰를 확인")
    public SuccessResponse<List<SearchReviewWriterReviewResponseVo>> searchReviewWriterReview(
        @PathVariable(value = "review_writer_uuid") String reviewWriterUuid) {
        List<SearchReviewWriterReviewResponseVo> searchReviewWriterReviewResponseVos = reviewService.searchReviewWriterReview(reviewWriterUuid);
        return new SuccessResponse<>(searchReviewWriterReviewResponseVos);
    }
}
