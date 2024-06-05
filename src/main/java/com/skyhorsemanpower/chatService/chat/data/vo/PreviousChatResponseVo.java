package com.skyhorsemanpower.chatService.chat.data.vo;

import com.skyhorsemanpower.chatService.chat.data.dto.PreviousChatDto;
import com.skyhorsemanpower.chatService.chat.data.dto.PreviousChatWithMemberInfoDto;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
public class PreviousChatResponseVo {
    private List<PreviousChatWithMemberInfoDto> previousChatWithMemberInfoDtos;
    private int currentPage;
    private boolean hasNext;

    public PreviousChatResponseVo(List<PreviousChatWithMemberInfoDto> previousChatWithMemberInfoDtos, int currentPage, boolean hasNext) {
        this.previousChatWithMemberInfoDtos = previousChatWithMemberInfoDtos;
        this.currentPage = currentPage;
        this.hasNext = hasNext;
    }
}
