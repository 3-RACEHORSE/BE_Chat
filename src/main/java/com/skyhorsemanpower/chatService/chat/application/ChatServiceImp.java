package com.skyhorsemanpower.chatService.chat.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skyhorsemanpower.chatService.chat.data.dto.ChatMemberDto;
import com.skyhorsemanpower.chatService.chat.data.dto.ChatRoomListElementDto;
import com.skyhorsemanpower.chatService.chat.data.dto.EnteringMemberDto;
import com.skyhorsemanpower.chatService.chat.data.dto.LeaveChatRoomDto;
import com.skyhorsemanpower.chatService.chat.data.dto.PreviousChatDto;
import com.skyhorsemanpower.chatService.chat.data.vo.ChatVo;
import com.skyhorsemanpower.chatService.chat.data.vo.LastChatVo;
import com.skyhorsemanpower.chatService.chat.data.vo.MemberInfoResponseVo;
import com.skyhorsemanpower.chatService.chat.data.vo.PreviousChatResponseVo;
import com.skyhorsemanpower.chatService.chat.domain.Chat;
import com.skyhorsemanpower.chatService.chat.domain.ChatRoom;
import com.skyhorsemanpower.chatService.chat.domain.ChatRoomMember;
import com.skyhorsemanpower.chatService.chat.infrastructure.ChatRepository;
import com.skyhorsemanpower.chatService.chat.infrastructure.ChatRoomRepository;
import com.skyhorsemanpower.chatService.chat.infrastructure.ChatSyncRepository;
import com.skyhorsemanpower.chatService.common.response.CustomException;
import com.skyhorsemanpower.chatService.common.response.ResponseStatus;
import com.skyhorsemanpower.chatService.common.ServerPathEnum;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

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

    public void createChatRoom(List<ChatMemberDto> chatMemberDtos) {
        if (chatMemberDtos.size() < 2) {
            throw new CustomException(ResponseStatus.NOT_ENOUGH_MEMBERS);
        }

        try {
            String roomNumber = UUID.randomUUID().toString();
            ChatRoom chatRoom = ChatRoom.builder()
                .roomNumber(roomNumber)
                .build();

            chatMemberDtos.forEach(dto -> {
                MemberInfoResponseVo memberInfoResponseVo = getMemberInfoByWebClientBlocking(dto.getMemberUuid());
                ChatRoomMember chatRoomMember = ChatRoomMember.builder()
                    .memberUuid(dto.getMemberUuid())
                    .memberHandle(memberInfoResponseVo.getHandle())
                    .memberProfileImage(memberInfoResponseVo.getProfileImage())
                    .chatRoom(chatRoom)
                    .build();
                chatRoom.addChatRoomMember(chatRoomMember);
            });

            chatRoomRepository.save(chatRoom);
        } catch (Exception e) {
            throw new CustomException(ResponseStatus.CREATE_CHATROOM_FAILED);
        }
    }

    @Override
    public void sendChat(ChatVo chatVo) {
//        verifyChatRoomAndMemberExistence(chatVo);

        boolean isRead = checkReadStatus(chatVo);
        saveChatMessage(chatVo, isRead);
    }

//    private void verifyChatRoomAndMemberExistence(ChatVo chatVo) {
//        boolean isMemberInChatRoom = chatRoomRepository.findByMemberUuidContainingAndRoomNumber(
//            chatVo.getSenderUuid(), chatVo.getRoomNumber()).isPresent();
//        if (!isMemberInChatRoom) {
//            throw new CustomException(ResponseStatus.WRONG_CHATROOM_AND_MEMBER);
//        }
//    }

    private boolean checkReadStatus(ChatVo chatVo) {
        String otherUuid = findOtherMemberUuid(chatVo.getSenderUuid(), chatVo.getRoomNumber());
        return isMemberDataExists(otherUuid, chatVo.getRoomNumber());
    }

    private void saveChatMessage(ChatVo chatVo, boolean isRead) {
        int readCount = isRead ? 0 : 1;
        Chat chat = Chat.builder()
            .senderUuid(chatVo.getSenderUuid())
            .content(chatVo.getContent())
            .roomNumber(chatVo.getRoomNumber())
            .createdAt(LocalDateTime.now())
            .readCount(readCount)
            .build();
        chatRepository.save(chat).subscribe();
        chatVo.setCreatedAt(chat.getCreatedAt());
        chatVo.setReadCount(chat.getReadCount());
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
//    @Override
//    public Flux<ChatRoomListElementDto> getChatRoomsByUserUuid(String userUuid) {
//        return Mono.fromCallable(() -> chatRoomRepository.findByMemberUuidsContaining(userUuid))
//            .flatMapMany(Flux::fromIterable)
////            .sort(Comparator.comparing(
////                ChatRoom::getLastChatTime,
////                Comparator.nullsLast(Comparator.reverseOrder())
////            ))
//            .map(chatRoom -> {
//                String otherUserUuid = chatRoom.getMemberUuids().stream()
//                    .filter(uuid -> !uuid.equals(userUuid))
//                    .findFirst()
//                    .orElse(null);
//                return ChatRoomListElementDto.fromEntityAndOtherUserUuid(chatRoom, otherUserUuid);
//            }).onErrorResume(e -> Flux.empty());
//        // 이거는 Flux라 CustomException 처리가 안됩니다 이렇게 빈 Flux로 반환해야하는듯
//    }

    @Override
    public PreviousChatResponseVo getPreviousChat(String roomNumber, LocalDateTime enterTime, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PreviousChatDto> previousChat= chatSyncRepository.findByRoomNumberAndCreatedAtBeforeOrderByCreatedAtDesc(roomNumber, enterTime, pageable);
        int currentPage = page;
        boolean hasNext = previousChat.hasNext();
        return new PreviousChatResponseVo(previousChat.getContent(), currentPage, hasNext);
        // Todo 현재는 senderUuid로 반환하지만 member의 프로필 사진과 핸들 반환하게 수정
    }

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

        List<ChatRoom> chatRooms = chatRoomRepository.findAllByRoomNumber(roomNumber);

        if (chatRooms.isEmpty()) {
            log.error("올바르지 않은 채팅방입니다. roomNumber={}, uuid={}", roomNumber, uuid);
            throw new CustomException(ResponseStatus.WRONG_CHATROOM_AND_MEMBER);
        }

//        for (ChatRoom chatRoom : chatRooms) {
//            if (!chatRoom.getMemberUuid().equals(uuid)) {
//                log.info("다른 멤버 UUID 찾음: {}", chatRoom.getMemberUuid());
//                return chatRoom.getMemberUuid();
//            }
//        }

        log.error("UUID가 채팅방에 포함되어 있지 않음: roomNumber={}, uuid={}", roomNumber, uuid);
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

    @Override
    public void leaveChatRoom(LeaveChatRoomDto leaveChatRoomDto) {
        String redisKey = "room:" + leaveChatRoomDto.getRoomNumber() + ":member:" + leaveChatRoomDto.getUuid();
        redisTemplate.delete(redisKey);
        log.info("Deleted entry for room {} and member {}", leaveChatRoomDto.getRoomNumber(), leaveChatRoomDto.getUuid());
    }
    @Override
    public LastChatVo getLastChat(String uuid, String roomNumber) {
        Optional<Chat> optionalChat = chatSyncRepository.findFirstByRoomNumberOrderByCreatedAtDesc(roomNumber);
        if (optionalChat.isPresent()) {
            return LastChatVo.builder()
                .content(optionalChat.get().getContent())
                .createdAt(optionalChat.get().getCreatedAt())
                .build();
        } else {
            throw new CustomException(ResponseStatus.NO_DATA);
        }
    }
    // webClient-blocking 통신으로 회원 서비스에 uuid를 이용해 handle과 프로필 이미지 데이터 요청
    private MemberInfoResponseVo getMemberInfoByWebClientBlocking(String uuid) {
        WebClient webClient = WebClient.create(ServerPathEnum.MEMBER_SERVER.getServer());

        ResponseEntity<MemberInfoResponseVo> responseEntity = webClient.get()
            .uri(uriBuilder -> uriBuilder.path(ServerPathEnum.GET_MEMBER_INFO.getServer() + "/{uuid}")
                .build(uuid))
            .retrieve().toEntity(MemberInfoResponseVo.class).block();
        return responseEntity.getBody();
    }
}