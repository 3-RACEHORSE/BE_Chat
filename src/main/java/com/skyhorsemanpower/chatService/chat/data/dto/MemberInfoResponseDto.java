package com.skyhorsemanpower.chatService.chat.data.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skyhorsemanpower.chatService.common.JsonPropertyEnum;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class MemberInfoResponseDto {

    @JsonProperty(value = JsonPropertyEnum.Constant.HANDLE)
    private String handle;

    @JsonProperty(value = JsonPropertyEnum.Constant.PROFILE)
    private String profileImage;
}
