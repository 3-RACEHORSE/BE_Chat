package com.skyhorsemanpower.chatService.chat.application;

import com.skyhorsemanpower.chatService.chat.data.dto.ChatMemberDto;
import com.skyhorsemanpower.chatService.chat.data.dto.ChatRoomListDto;
import com.skyhorsemanpower.chatService.chat.data.dto.ChatRoomListElementDto;
import com.skyhorsemanpower.chatService.chat.data.vo.ChatVo;
import com.skyhorsemanpower.chatService.chat.domain.Chat;
import com.skyhorsemanpower.chatService.chat.domain.ChatRoom;
import com.skyhorsemanpower.chatService.chat.infrastructure.ChatRepository;
import com.skyhorsemanpower.chatService.chat.infrastructure.ChatRoomRepository;
import com.skyhorsemanpower.chatService.common.CustomException;
import com.skyhorsemanpower.chatService.common.ResponseStatus;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatServiceImp implements ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRepository chatRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public boolean createChatRoom(List<ChatMemberDto> chatMemberDtos) {
        if (chatMemberDtos.size() < 2) {
            throw new CustomException(ResponseStatus.NOT_ENOUGH_MEMBERS);
        }

        try {
            String roomNumber = UUID.randomUUID().toString();
            Set<String> memberUuids = chatMemberDtos.stream()
                .map(ChatMemberDto::getMemberUuid)
                .collect(Collectors.toSet());

            ChatRoom chatRoom = ChatRoom.builder()
                .roomNumber(roomNumber)
                .memberUuids(memberUuids)
                .build();

            chatRoomRepository.save(chatRoom);
            return true;
        } catch (Exception e) {
            throw new CustomException(ResponseStatus.CREATE_CHATROOM_FAILED);
        }
    }

    @Override
    public void sendChat(ChatVo chatVo) {
        log.info("chatVo: {}", chatVo);
        try {
            // 새 채팅 메시지 생성 및 저장
            Chat chat = Chat.builder()
                .senderUuid(chatVo.getSenderUuid())
                .content(chatVo.getContent())
                .roomNumber(chatVo.getRoomNumber())
                .createdAt(LocalDateTime.now())
                .build();
            chatRepository.save(chat).subscribe();

            // 마지막 메시지 및 시간 업데이트
            Optional<ChatRoom> chatRoomOpt = chatRoomRepository.findByRoomNumber(chatVo.getRoomNumber());
            if (chatRoomOpt.isPresent()) {
                ChatRoom chatRoom = chatRoomOpt.get();
                chatRoom.updateLastMessage(chatVo.getContent(), chatVo.getCreatedAt());
                chatRoomRepository.save(chatRoom);

                // 사용자에게 실시간으로 메시지 전송 및 리스트 재정렬
                List<ChatRoom> userChatRooms = chatRoomRepository.findByMemberUuidsContaining(chatVo.getSenderUuid());
                userChatRooms.sort(Comparator.comparing(ChatRoom::getLastMessageTime).reversed());

                List<ChatRoomListDto> chatRoomListDtos = userChatRooms.stream()
                    .map(ChatRoomListDto::fromEntity)
                    .collect(Collectors.toList());

                log.info("정렬: {}", chatRoomListDtos);

                // 사용자의 모든 채팅방 목록을 재정렬 후 전송
                for (ChatRoom room : userChatRooms) {
                    for (String memberUuid : room.getMemberUuids()) {
                        log.info("프론트에게 memberUuid에 맞춰 보내기: {}", memberUuid);
                        messagingTemplate.convertAndSendToUser(memberUuid, "/queue/chat-rooms", chatRoomListDtos);
                    }
                }
            } else {
                log.error("채팅방을 찾을 수 없습니다: {}", chatVo.getRoomNumber());
                throw new CustomException(ResponseStatus.CANNOT_FIND_CHATROOM);
            }
        } catch (Exception e) {
            log.error("채팅 보내기 중 오류 발생: {}", chatVo, e);
            throw new CustomException(ResponseStatus.SAVE_CHAT_FAILED);
        }
    }

    @Override
    public Flux<ChatVo> getChat(String roomNumber) {
        return chatRepository.findChatByRoomNumber(roomNumber)
            .subscribeOn(Schedulers.boundedElastic())
            .onErrorResume(throwable -> {
                log.error("채팅 불러오기 중 오류 발생: {}", roomNumber, throwable);
                return Flux.error(new CustomException(ResponseStatus.LOAD_CHAT_FAILED));
            });
    }

    @Override
    public Flux<ChatRoomListElementDto> getChatRoomsByUserUuid(String userUuid) {
        return Flux.fromIterable(chatRoomRepository.findByMemberUuidsContaining(userUuid))
            .sort(Comparator.comparing(ChatRoom::getLastMessageTime).reversed())
            .map(chatRoom -> {
                String otherUserUuid = null;
                for (String uuid : chatRoom.getMemberUuids()) {
                    if (!uuid.equals(userUuid)) {
                        otherUserUuid = uuid;
                        break;
                    }
                }
                return ChatRoomListElementDto.fromEntityAndOtherUserUuid(chatRoom, otherUserUuid);
            });
    }
}