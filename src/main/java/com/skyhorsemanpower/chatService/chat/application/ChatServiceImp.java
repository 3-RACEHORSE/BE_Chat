package com.skyhorsemanpower.chatService.chat.application;

import com.skyhorsemanpower.chatService.chat.data.dto.ChatMemberDto;
import com.skyhorsemanpower.chatService.chat.domain.ChatRoom;
import com.skyhorsemanpower.chatService.chat.infrastructure.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatServiceImp implements ChatService{
    private final ChatRoomRepository chatRoomRepository;
    @Override
    public boolean createChatRoom(ChatMemberDto chatMemberDto) {
        ChatRoom chatRoom = ChatRoom.builder()
            .chatMember1(chatMemberDto.getFirstMemberUuid())
            .chatMember2(chatMemberDto.getSecondMemberUuid())
//            .createdAt() // createdAt 얘네들은 옛날에는 상속받아서 했는데
            .lastChat(null)
//            .lastChatCreatedAt() // 얘도
            .build();
        try {
            chatRoomRepository.save(chatRoom);
            return true;
        } catch (Exception e) {
            return false;
        }

    }
}
