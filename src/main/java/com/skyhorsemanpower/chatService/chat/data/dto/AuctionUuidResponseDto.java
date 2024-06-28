package com.skyhorsemanpower.chatService.chat.data.dto;

import com.skyhorsemanpower.chatService.chat.data.vo.AuctionUuidResponseVo;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuctionUuidResponseDto {

    private String auctionUuid;

    public static AuctionUuidResponseVo dtoToVo(AuctionUuidResponseDto auctionUuidResponseDto) {
        return AuctionUuidResponseVo.builder()
            .auctionUuid(auctionUuidResponseDto.getAuctionUuid())
            .build();
    }
}
