package com.skyhorsemanpower.chatService.chat.application;

import com.skyhorsemanpower.chatService.chat.data.dto.ChatMemberDto;

public interface ChatService {
    boolean createChatRoom(ChatMemberDto chatMemberDto);
}
