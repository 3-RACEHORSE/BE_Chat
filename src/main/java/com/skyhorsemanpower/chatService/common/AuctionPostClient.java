package com.skyhorsemanpower.chatService.common;


import com.skyhorsemanpower.chatService.chat.data.vo.AuctionInfoResponseVo;
import com.skyhorsemanpower.chatService.common.ServerPathEnum.Constant;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "auctionPostClient", url = Constant.AUCTION_POST_SERVER)
public interface AuctionPostClient {
    @GetMapping(Constant.AUCTION_INFO)
    AuctionInfoResponseVo getAuctionInfo(@PathVariable("auctionUuid") String uuid);
}
