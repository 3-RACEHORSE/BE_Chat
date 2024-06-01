package com.skyhorsemanpower.chatService.chat.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skyhorsemanpower.chatService.chat.data.dto.ChatMemberDto;
import com.skyhorsemanpower.chatService.chat.data.dto.ChatRoomListDto;
import com.skyhorsemanpower.chatService.chat.data.dto.ChatRoomListElementDto;
import com.skyhorsemanpower.chatService.chat.data.dto.EnteringMemberDto;
import com.skyhorsemanpower.chatService.chat.data.vo.ChatVo;
import com.skyhorsemanpower.chatService.chat.domain.Chat;
import com.skyhorsemanpower.chatService.chat.domain.ChatRoom;
import com.skyhorsemanpower.chatService.chat.infrastructure.ChatRepository;
import com.skyhorsemanpower.chatService.chat.infrastructure.ChatRoomRepository;
import com.skyhorsemanpower.chatService.chat.infrastructure.ChatSyncRepository;
import com.skyhorsemanpower.chatService.common.CustomException;
import com.skyhorsemanpower.chatService.common.ResponseStatus;
import jakarta.websocket.OnOpen;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatServiceImp implements ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRepository chatRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatSyncRepository chatSyncRepository;
    private final Sinks.Many<ChatVo> sink = Sinks.many().multicast().onBackpressureBuffer();
    private final RedisTemplate<String, String> redisTemplate;
    private final MongoTemplate mongoTemplate;

    @Override
    public void createChatRoom(List<ChatMemberDto> chatMemberDtos) {
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
        } catch (Exception e) {
            throw new CustomException(ResponseStatus.CREATE_CHATROOM_FAILED);
        }
    }

    @Override
    public void sendChat(ChatVo chatVo) {
        boolean isMemberInChatRoom = chatRoomRepository.findByMemberUuidsContainingAndRoomNumber(
            chatVo.getSenderUuid(), chatVo.getRoomNumber()).isPresent();
        if (!isMemberInChatRoom) {
            throw new CustomException(ResponseStatus.WRONG_CHATROOM_AND_MEMBER);
        }
        String otherUuid = findOtherMemberUuid(chatVo.getSenderUuid(), chatVo.getRoomNumber());
        log.info("otherUuid : {}", otherUuid);
        boolean isRead = isMemberDataExists(otherUuid, chatVo.getRoomNumber());
        try {
            if (isRead) {
                // 새 채팅 메시지 생성 및 저장
                Chat chat = Chat.builder()
                    .senderUuid(chatVo.getSenderUuid())
                    .content(chatVo.getContent())
                    .roomNumber(chatVo.getRoomNumber())
                    .createdAt(LocalDateTime.now())
                    .readCount(0)
                    .build();
                chatRepository.save(chat).subscribe();
                chatVo.setCreatedAt(chat.getCreatedAt());
                chatVo.setReadCount(chat.getReadCount());
//                log.info("메시지 발행: {}", chatVo);
//                sink.tryEmitNext(chatVo);
            } else {
                // 새 채팅 메시지 생성 및 저장
                Chat chat = Chat.builder()
                    .senderUuid(chatVo.getSenderUuid())
                    .content(chatVo.getContent())
                    .roomNumber(chatVo.getRoomNumber())
                    .createdAt(LocalDateTime.now())
                    .readCount(1)
                    .build();
                chatRepository.save(chat).subscribe();
                chatVo.setCreatedAt(chat.getCreatedAt());
                chatVo.setReadCount(chat.getReadCount());
//                log.info("메시지 발행: {}", chatVo);
//                sink.tryEmitNext(chatVo);
            }

        } catch (Exception e) {
            log.error("채팅 보내기 중 오류 발생: {}", chatVo, e);
            throw new CustomException(ResponseStatus.SAVE_CHAT_FAILED);
        }
        // 마지막 메시지 및 시간 업데이트
        Optional<ChatRoom> chatRoomOpt = chatRoomRepository.findByRoomNumber(chatVo.getRoomNumber());
        if (chatRoomOpt.isPresent()) {
            ChatRoom chatRoom = chatRoomOpt.get();
            log.info("chatVo: {}", chatVo);
            log.info(String.valueOf(chatVo.getCreatedAt()));
            log.info(String.valueOf(chatVo.getContent()));
            chatRoom.updateLastChat(chatVo.getContent(), chatVo.getCreatedAt());
            chatRoomRepository.save(chatRoom);

            // 사용자에게 실시간으로 메시지 전송 및 리스트 재정렬
            List<ChatRoom> userChatRooms = chatRoomRepository.findByMemberUuidsContaining(chatVo.getSenderUuid());
            userChatRooms.sort(Comparator.comparing(ChatRoom::getLastChatTime).reversed());

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
    }
    @Override
    public Flux<ChatVo> getChat(String roomNumber, String uuid) {
        enteringMember(uuid, roomNumber);
        changeReadCount(roomNumber, uuid);
        LocalDateTime now = LocalDateTime.now();
        return chatRepository.findChatByRoomNumberAndCreatedAtOrAfter(roomNumber, now);
//        return sink.asFlux()
//            .filter(chat -> chat.getRoomNumber().equals(roomNumber))
//            .subscribeOn(Schedulers.boundedElastic())
//            .doOnTerminate(() -> log.info("Chat stream for room {} terminated.", roomNumber))
//            .doOnError(error -> log.error("Chat stream for room {} encountered error: {}", roomNumber, error.getMessage()))
//            .doFinally(signalType -> log.info("Chat stream for room {} completed with signal: {}", roomNumber, signalType));
    }
    @Override
    public Flux<ChatRoomListElementDto> getChatRoomsByUserUuid(String userUuid) {
        return Mono.fromCallable(() -> chatRoomRepository.findByMemberUuidsContaining(userUuid))
            .flatMapMany(Flux::fromIterable)
            .sort(Comparator.comparing(
                ChatRoom::getLastChatTime,
                Comparator.nullsLast(Comparator.reverseOrder())
            ))
            .map(chatRoom -> {
                String otherUserUuid = chatRoom.getMemberUuids().stream()
                    .filter(uuid -> !uuid.equals(userUuid))
                    .findFirst()
                    .orElse(null);
                return ChatRoomListElementDto.fromEntityAndOtherUserUuid(chatRoom, otherUserUuid);
            }).onErrorResume(e -> Flux.empty());
        // 이거는 Flux라 CustomException 처리가 안됩니다 이렇게 빈 Flux로 반환해야하는듯
    }

    @Override
    public Page<ChatVo> getPreviousChat(String roomNumber, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Optional<ChatRoom> chatRoomOpt = chatRoomRepository.findByRoomNumber(roomNumber);
        if (chatRoomOpt.isPresent()) {
            return chatSyncRepository.findByRoomNumberOrderByCreatedAtDesc(roomNumber, pageable);
        } else {
            throw new CustomException(ResponseStatus.WRONG_CHATROOM_AND_MEMBER);
        }
    }

//    @Override
//    public void enteringMember(String uuid, String roomNumber) {
//        log.info("enteringMember 실행: roomNumber={}, uuid={}", roomNumber, uuid);
//        try {
//            log.info("try 진입: roomNumber={}, uuid={}", roomNumber, uuid);
//            EnteringMember enteringMember = EnteringMember.builder()
//                .uuid(uuid)
//                .roomNumber(roomNumber)
//                .enterTime(LocalDateTime.now())
//                .build();
//            log.info("enteringMember : {}", enteringMember);
//
//            // 추가적인 로그
//            log.info("Redis에 데이터를 저장하기 전: {}", enteringMember);
//
//            redisEnteringMemberRepository.save(enteringMember);
//        } catch (Exception e) {
//            log.error("Redis에 데이터 저장 중 오류 발생", e);
//            throw new CustomException(ResponseStatus.REDIS_DB_ERROR);
//        }
//    }
    @Override
    public void enteringMember(String uuid, String roomNumber) {
        String otherUuid = findOtherMemberUuid(uuid, roomNumber);
        log.info("otherUuid : {}", otherUuid);
        log.info("enteringMember 실행: roomNumber={}, uuid={}", roomNumber, uuid);
        try {
            // 데이터를 JSON 형식으로 변환
            ObjectMapper objectMapper = new ObjectMapper();
            EnteringMemberDto enteringMemberDto =
                EnteringMemberDto.builder()
                    .roomNumber(roomNumber)
                    .uuid(uuid)
                    .build();
            String jsonData = objectMapper.writeValueAsString(enteringMemberDto);

            // Redis에 데이터 저장
            String redisKey = "room:" + roomNumber + ":member:" + uuid;
            redisTemplate.opsForValue().set(redisKey, jsonData);

        } catch (Exception e) {
            log.error("Redis에 데이터 저장 중 오류 발생", e);
            throw new CustomException(ResponseStatus.REDIS_DB_ERROR);
        }
    }
    @Override
    public String findOtherMemberUuid(String uuid, String roomNumber) {
        log.info("findOtherMemberUuid 시작: uuid={}, roomNumber={}", uuid, roomNumber);
        Optional<ChatRoom> chatRoomOptional = chatRoomRepository.findByRoomNumber(roomNumber);
        if (chatRoomOptional.isPresent()) {
            ChatRoom chatRoom = chatRoomOptional.get();
            if (!chatRoom.getMemberUuids().contains(uuid)) {
                log.error("UUID가 채팅방에 포함되어 있지 않음: roomNumber={}, uuid={}", roomNumber, uuid);
                throw new CustomException(ResponseStatus.WRONG_CHATROOM_AND_MEMBER);
            }
            for (String memberUuid : chatRoom.getMemberUuids()) {
                if (!memberUuid.equals(uuid)) {
                    log.info("다른 멤버 UUID 찾음: {}", memberUuid);
                    return memberUuid;
                }
            }
        }
        log.error("올바르지 않은 채팅방 또는 멤버입니다. roomNumber={}, uuid={}", roomNumber, uuid);
        throw new CustomException(ResponseStatus.WRONG_CHATROOM_AND_MEMBER);
    }

    private boolean isMemberDataExists(String uuid, String roomNumber) {
        try {
            String redisKey = "room:" + roomNumber + ":member:" + uuid;
            Boolean exists = redisTemplate.hasKey(redisKey);

            if (exists != null && exists) {
                log.info("Redis에 데이터가 존재합니다: roomNumber={}, uuid={}", roomNumber, uuid);
                return true;
            } else {
                log.info("Redis에 데이터가 존재하지 않습니다: roomNumber={}, uuid={}", roomNumber, uuid);
                return false;
            }
        } catch (Exception e) {
            log.error("Redis에서 데이터 존재 여부 확인 중 오류 발생", e);
            throw new CustomException(ResponseStatus.REDIS_DB_ERROR);
        }
    }

    @Override
    public int getUnreadChatCount(String roomNumber, String uuid) {
        String otherUuid = findOtherMemberUuid(uuid, roomNumber);
        int readCount = 1;
        try {
            List<Chat> chats = chatSyncRepository.findAllByRoomNumberAndSenderUuidAndReadCount(
                roomNumber, otherUuid, readCount);
            return chats.size();
        } catch (Exception e) {
            throw new CustomException(ResponseStatus.MONGO_DB_ERROR);
        }
    }

    public void changeReadCount(String roomNumber, String uuid) {
        String otherUuid = findOtherMemberUuid(uuid, roomNumber);
        Query query = new Query();
        query.addCriteria(Criteria.where("roomNumber").is(roomNumber)
            .and("senderUuid").is(otherUuid));

        List<Chat> chats = mongoTemplate.find(query, Chat.class);
        if (chats.isEmpty()) {
            return;
        }

        Update update = new Update();
        update.set("readCount", 0);

        mongoTemplate.updateMulti(query, update, Chat.class);
    }
}