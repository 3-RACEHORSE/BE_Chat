package com.skyhorsemanpower.chatService.review.data.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberInfoResponseDto {

    private String email;
    private String name;
    private String phoneNum;
    private String profileImage;

}
