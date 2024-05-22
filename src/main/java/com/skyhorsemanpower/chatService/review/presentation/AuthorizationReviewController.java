package com.skyhorsemanpower.chatService.review.presentation;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/authorization/review")
@Tag(name = "인가가 필요한 리뷰", description = "인가가 필요한 리뷰 관련 API")
@Slf4j
public class AuthorizationReviewController {

}
