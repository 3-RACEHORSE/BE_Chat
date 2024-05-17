package com.skyhorsemanpower.chatService.chat.application;

import com.skyhorsemanpower.chatService.chat.data.dto.ChatMemberDto;
import com.skyhorsemanpower.chatService.chat.data.dto.ChatRoomWithLastChatDto;
import com.skyhorsemanpower.chatService.chat.data.vo.ChatRoomVo;
import com.skyhorsemanpower.chatService.chat.data.vo.ChatVo;
import com.skyhorsemanpower.chatService.chat.data.vo.LastChatVo;
import com.skyhorsemanpower.chatService.chat.domain.Chat;
import com.skyhorsemanpower.chatService.chat.domain.ChatRoom;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ChatService {
    boolean createChatRoom(List<ChatMemberDto> chatMemberDtos);
    void sendChat(ChatVo chatVo);
    Flux<ChatVo> getChat(String roomNumber);
    Mono<ChatVo> getLastChat(String roomNumber);
}
