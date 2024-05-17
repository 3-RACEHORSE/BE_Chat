package com.skyhorsemanpower.chatService.chat.application;

import com.skyhorsemanpower.chatService.chat.data.dto.ChatMemberDto;
import com.skyhorsemanpower.chatService.chat.data.dto.ChatRoomWithLastChatDto;
import com.skyhorsemanpower.chatService.chat.data.vo.ChatRoomVo;
import com.skyhorsemanpower.chatService.chat.data.vo.ChatVo;
import com.skyhorsemanpower.chatService.chat.data.vo.LastChatVo;
import com.skyhorsemanpower.chatService.chat.domain.Chat;
import com.skyhorsemanpower.chatService.chat.domain.ChatRoom;
import com.skyhorsemanpower.chatService.chat.infrastructure.ChatRepository;
import com.skyhorsemanpower.chatService.chat.infrastructure.ChatRoomRepository;
import com.skyhorsemanpower.chatService.common.CustomException;
import com.skyhorsemanpower.chatService.common.ResponseStatus;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.publisher.Sinks.Many;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatServiceImp implements ChatService{
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRepository chatRepository;
    private final ConcurrentHashMap<String, Many<ChatVo>> latestMessages;

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
    @Override
    public void sendChat(ChatVo chatVo) {
        log.info("chatVo: {}", chatVo);
        try {
            Chat chat = Chat.builder()
                .senderUuid(chatVo.getSenderUuid())
                .content(chatVo.getContent())
                .roomNumber(chatVo.getRoomNumber())
                .createdAt(LocalDateTime.now())
                .build();
            chatRepository.save(chat).subscribe();
        } catch (Exception e) {
            log.error("채팅 보내기 중 오류 발생: {}", chatVo);
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
    public Mono<ChatVo> getLastChat(String roomNumber) {
        return chatRepository.findFirstByRoomNumberOrderByCreatedAtDesc(roomNumber)
            .subscribeOn(Schedulers.boundedElastic())
            .onErrorResume(throwable -> {
                log.error("채팅 불러오기 중 오류 발생: {}", roomNumber, throwable);
                return Mono.error(new CustomException(ResponseStatus.LOAD_CHAT_FAILED));
            });
    }
}
