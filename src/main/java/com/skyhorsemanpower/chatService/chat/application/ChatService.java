package com.skyhorsemanpower.chatService.chat.application;

import com.skyhorsemanpower.chatService.chat.data.dto.ChatMemberDto;
import com.skyhorsemanpower.chatService.chat.data.dto.ChatRoomWithLastChatDto;
import com.skyhorsemanpower.chatService.chat.data.vo.ChatVo;
import com.skyhorsemanpower.chatService.chat.domain.Chat;
import java.util.List;
import reactor.core.publisher.Flux;

public interface ChatService {
    boolean createChatRoom(List<ChatMemberDto> chatMemberDtos);
    void sendChat(ChatVo chatVo);
    Flux<ChatVo> getChat(String roomNumber);

    List<ChatRoomWithLastChatDto> getAllChatRoomsWithLastChat(String memberUuid);

    Flux<Chat> getLastChatInRoom(String roomNumber);
}
