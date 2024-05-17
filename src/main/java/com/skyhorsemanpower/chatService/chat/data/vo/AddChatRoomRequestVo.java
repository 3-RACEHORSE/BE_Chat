package com.skyhorsemanpower.chatService.chat.data.vo;

import com.skyhorsemanpower.chatService.chat.data.dto.ChatMemberDto;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;


@Getter
public class AddChatRoomRequestVo {
    private List<String> chatUserUuids;

    public AddChatRoomRequestVo() {
    }

    @Builder
    public AddChatRoomRequestVo(List<String> chatUserUuids) {
        this.chatUserUuids = chatUserUuids;
    }

    public List<ChatMemberDto> toChatMemberDto() {
        if (chatUserUuids.size() < 2) {
            throw new IllegalArgumentException("최소 2명이 있어야 채팅방을 생성할 수 있습니다");
        }
        List<ChatMemberDto> chatMemberDtos = new ArrayList<>();
        for (String uuid : chatUserUuids) {
            chatMemberDtos.add(ChatMemberDto.builder()
                .memberUuid(uuid)
                .build());
        }
        return chatMemberDtos;
    }
}


