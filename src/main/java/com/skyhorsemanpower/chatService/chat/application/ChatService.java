package com.skyhorsemanpower.chatService.chat.application;

import com.skyhorsemanpower.chatService.chat.data.dto.ChatMemberDto;
import com.skyhorsemanpower.chatService.chat.data.dto.ChatRoomListDto;
import com.skyhorsemanpower.chatService.chat.data.dto.ChatRoomListElementDto;
import com.skyhorsemanpower.chatService.chat.data.vo.ChatVo;
import java.util.List;
import org.springframework.data.domain.Page;
import reactor.core.publisher.Flux;

public interface ChatService {
    void createChatRoom(List<ChatMemberDto> chatMemberDtos);
    void sendChat(ChatVo chatVo);
    Flux<ChatVo> getChat(String roomNumber, String uuid);
//    List<Flux<ChatRoomVo>> getChatRoomList(String memberUuid);
    Flux<ChatRoomListElementDto> getChatRoomsByUserUuid(String userUuid);

    Page<ChatVo> getPreviousChat(String roomNumber, int page, int size);
    void enteringMember(String uuid, String roomNumber);
    String findOtherMemberUuid(String uuid, String roomNumber);
}
