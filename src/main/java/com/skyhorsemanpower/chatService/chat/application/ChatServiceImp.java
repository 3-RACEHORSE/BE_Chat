package com.skyhorsemanpower.chatService.chat.application;

import com.skyhorsemanpower.chatService.chat.data.dto.ChatMemberDto;
import com.skyhorsemanpower.chatService.chat.domain.ChatRoom;
import com.skyhorsemanpower.chatService.chat.infrastructure.ChatRoomRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatServiceImp implements ChatService{
    private final ChatRoomRepository chatRoomRepository;
    @Override
    public boolean createChatRoom(List<ChatMemberDto> chatMemberDtos) {
        if (chatMemberDtos.size() < 2) {
            throw new IllegalArgumentException("최소 2명의 회원이 필요합니다.");
        }

        try {
            String roomNumber = UUID.randomUUID().toString();

            for (ChatMemberDto chatMemberDto : chatMemberDtos) { // 리스트에서 꺼내어 반복
                String userUuid = chatMemberDto.getMemberUuid();

                ChatRoom chatRoom = ChatRoom.builder()
                    .roomNumber(roomNumber)
                    .memberUuid(userUuid)
                    .build();

                chatRoomRepository.save(chatRoom);
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
