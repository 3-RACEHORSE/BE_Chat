package com.skyhorsemanpower.chatService.common;

import com.skyhorsemanpower.chatService.common.ServerPathEnum.Constant;
import com.skyhorsemanpower.chatService.review.data.dto.MemberInfoResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "memberPostClient", url = Constant.MEMBER_SERVER)
public interface MemberPostClient {
    @GetMapping(Constant.MEMBER_INFO)
    MemberInfoResponseDto getMemberInfo(@RequestHeader String uuid, @RequestHeader String authorization);
}
