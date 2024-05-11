package com.skyhorsemanpower.chatService.chat.application;

import com.skyhorsemanpower.chatService.chat.data.dto.ChatMemberDto;
import com.skyhorsemanpower.chatService.common.ExceptionResponse;
import java.util.List;

public interface ChatService {
    boolean createChatRoom(List<ChatMemberDto> chatMemberDtos);
}
