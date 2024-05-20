package com.skyhorsemanpower.chatService.chat.application;

import com.skyhorsemanpower.chatService.chat.data.dto.ChatMemberDto;
import com.skyhorsemanpower.chatService.chat.data.dto.ChatRoomListDto;
import com.skyhorsemanpower.chatService.chat.data.vo.ChatVo;
import java.util.List;
import reactor.core.publisher.Flux;

public interface ChatService {
    boolean createChatRoom(List<ChatMemberDto> chatMemberDtos);
    void sendChat(ChatVo chatVo);
    Flux<ChatVo> getChat(String roomNumber);
//    List<Flux<ChatRoomVo>> getChatRoomList(String memberUuid);
    Flux<ChatRoomListDto> getChatRoomsByUserUuid(String userUuid);
}
