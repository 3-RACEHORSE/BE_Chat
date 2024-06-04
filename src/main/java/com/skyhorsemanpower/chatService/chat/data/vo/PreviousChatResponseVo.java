package com.skyhorsemanpower.chatService.chat.data.vo;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
public class PreviousChatResponseVo {
    private List<ChatVo> content;
    private int currentPage;
    private boolean hasNext;

    public PreviousChatResponseVo(List<ChatVo> content, int currentPage, boolean hasNext) {
        this.content = content;
        this.currentPage = currentPage;
        this.hasNext = hasNext;
    }
}
