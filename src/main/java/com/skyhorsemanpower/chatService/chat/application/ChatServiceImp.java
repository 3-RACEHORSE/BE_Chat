package com.skyhorsemanpower.chatService.chat.application;

import com.mongodb.client.model.changestream.OperationType;
import com.skyhorsemanpower.chatService.chat.data.dto.BeforeChatRoomDto;
import com.skyhorsemanpower.chatService.chat.data.dto.ChatRoomMemberResponseDto;
import com.skyhorsemanpower.chatService.chat.data.dto.UpdateProfileImageRequestDto;
import com.skyhorsemanpower.chatService.chat.data.dto.ChatRoomTitleResponseDto;
import com.skyhorsemanpower.chatService.chat.data.dto.LeaveChatRoomDto;
import com.skyhorsemanpower.chatService.chat.data.dto.PreviousChatDto;
import com.skyhorsemanpower.chatService.chat.data.dto.PreviousChatWithMemberInfoDto;
import com.skyhorsemanpower.chatService.chat.data.dto.SendChatRequestDto;
import com.skyhorsemanpower.chatService.chat.data.vo.ChatRoomResponseVo;
import com.skyhorsemanpower.chatService.chat.data.dto.SendToAlarmDto;
import com.skyhorsemanpower.chatService.chat.data.vo.GetChatVo;
import com.skyhorsemanpower.chatService.chat.data.vo.LastChatVo;
import com.skyhorsemanpower.chatService.chat.data.vo.PreviousChatResponseVo;
import com.skyhorsemanpower.chatService.chat.domain.Chat;
import com.skyhorsemanpower.chatService.chat.domain.ChatRoom;
import com.skyhorsemanpower.chatService.chat.domain.ChatRoomMember;
import com.skyhorsemanpower.chatService.chat.infrastructure.ChatRepository;
import com.skyhorsemanpower.chatService.chat.infrastructure.ChatRoomMemberRepository;
import com.skyhorsemanpower.chatService.chat.infrastructure.ChatRoomRepository;
import com.skyhorsemanpower.chatService.chat.infrastructure.ChatSyncRepository;
import com.skyhorsemanpower.chatService.common.AuctionPostClient;
import com.skyhorsemanpower.chatService.common.RandomHandleGenerator;
import com.skyhorsemanpower.chatService.common.ServerPathEnum.Constant;
import com.skyhorsemanpower.chatService.common.kafka.KafkaProducerCluster;
import com.skyhorsemanpower.chatService.common.response.CustomException;
import com.skyhorsemanpower.chatService.common.response.ResponseStatus;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.ChangeStreamEvent;
import org.springframework.data.mongodb.core.ChangeStreamOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatServiceImp implements ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRepository chatRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final ChatSyncRepository chatSyncRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final ReactiveMongoTemplate reactiveMongoTemplate;
    private final MongoTemplate mongoTemplate;
    private final AuctionPostClient auctionPostClient;
    private final KafkaProducerCluster producer;


    @Override
    public void createChatRoom(BeforeChatRoomDto beforeChatRoomDto) {
        try {
            String roomNumber = UUID.randomUUID().toString();

            // 채팅방 회원 만들기
            List<ChatRoomMember> chatRoomMembers = beforeChatRoomDto.getMemberUuidsWithProfiles()
                .entrySet().stream()
                .map(entry -> {
                    String memberUuid = entry.getKey();
                    String profileImage = entry.getValue();
                    // 관련된 로직을 추가하여 handle을 생성하고, 회원 정보를 채팅방 회원 객체로 매핑합니다.
                    String handle = RandomHandleGenerator.generateRandomWord();

                    return ChatRoomMember.builder()
                        .memberUuid(memberUuid)
                        .memberHandle(handle)
                        .memberProfileImage(profileImage)
                        .roomNumber(roomNumber)
                        .lastReadTime(LocalDateTime.now())
                        .build();
                }).collect(Collectors.toList());

            // 관리자 채팅방 회원 객체 생성
            ChatRoomMember admin = ChatRoomMember.builder()
                .memberUuid(beforeChatRoomDto.getAdminUuid())
                .memberHandle("관리자")
                .memberProfileImage(beforeChatRoomDto.getThumbnail())
                .roomNumber(roomNumber)
                .lastReadTime(LocalDateTime.now())
                .build();

            // 관리자 객체를 chatRoomMembers 리스트에 추가
            chatRoomMembers.add(admin);

            // 채팅방 저장
            ChatRoom chatRoom = ChatRoom.builder()
                .roomNumber(roomNumber)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .chatRoomMembers(chatRoomMembers)
                .title(beforeChatRoomDto.getTitle() + " 공지방")
                .thumbnail(beforeChatRoomDto.getThumbnail())
                .build();

            chatRoomRepository.save(chatRoom);
            log.info("ChatRoom 저장완료: {}", roomNumber);

            // 채팅방 회원 저장
            chatRoomMembers.forEach(chatRoomMember -> {
                chatRoomMemberRepository.save(chatRoomMember);
                log.info("chatRoomMember 저장완료: {}", chatRoomMember);
            });
            sendToAlarm(beforeChatRoomDto, roomNumber);
        } catch (Exception e) {
            log.error("채팅 방 생성중 오류 발생: {}", e.getMessage());
            throw new CustomException(ResponseStatus.CREATE_CHATROOM_FAILED);
        }
    }


    @Transactional
    @Override
    public void sendChat(SendChatRequestDto sendChatRequestDto, String uuid) {
        // 채팅방의 회원인지 확인
        verifyChatRoomAndMemberExistence(sendChatRequestDto, uuid);
        if (sendChatRequestDto.getContent().isEmpty()) {
            log.info("빈 채팅");
        } else {
            // 채팅 저장
            saveChatMessage(sendChatRequestDto, uuid);
        }

    }

    private void verifyChatRoomAndMemberExistence(SendChatRequestDto sendChatRequestDto,
        String uuid) {
        // chatRoom에 조회
        boolean isMemberInChatRoom = chatRoomMemberRepository.findByMemberUuidAndRoomNumber(
            uuid, sendChatRequestDto.getRoomNumber()).isPresent();
        log.info("isMemberInChatRoom : {}", isMemberInChatRoom);
        if (!isMemberInChatRoom) {
            throw new CustomException(ResponseStatus.WRONG_CHATROOM_AND_MEMBER);
        }
    }

    @Transactional
    protected void saveChatMessage(SendChatRequestDto sendChatRequestDto, String uuid) {
        log.info("saveChatMessage 시작");
        try {
            Chat chat = Chat.builder()
                .senderUuid(uuid)
                .content(sendChatRequestDto.getContent())
                .roomNumber(sendChatRequestDto.getRoomNumber())
                .createdAt(LocalDateTime.now())
                .build();
            chatRepository.save(chat).subscribe();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CustomException(ResponseStatus.SAVE_CHAT_FAILED);
        }

    }

    @Override
    public Flux<GetChatVo> getChat(String roomNumber, String uuid) {
        ChatRoomMember chatRoomMember = chatRoomMemberRepository.findByMemberUuidAndRoomNumber(
                uuid, roomNumber)
            .orElseThrow(() -> new CustomException(ResponseStatus.WRONG_CHATROOM_AND_MEMBER));

        LocalDateTime now = LocalDateTime.now();

        return chatRepository.findChatByRoomNumberAndCreatedAtOrAfterOrdOrderByCreatedAtDesc(
                roomNumber, now)
            .flatMap(chatVo -> {
                String senderUuid = chatVo.getSenderUuid();
                return Mono.justOrEmpty(
                        chatRoomMemberRepository.findByMemberUuidAndRoomNumber(senderUuid, roomNumber))
                    .switchIfEmpty(
                        Mono.error(new CustomException(ResponseStatus.WRONG_CHATROOM_AND_MEMBER)))
                    .map(sender -> {
                        GetChatVo getChatVo = GetChatVo.builder()
                            .uuid(senderUuid)
                            .handle(sender.getMemberHandle())
                            .profileImage(sender.getMemberProfileImage())
                            .content(chatVo.getContent())
                            .createdAt(chatVo.getCreatedAt())
                            .build();
                        return getChatVo;
                    });
            });
    }


    @Override
    public List<ChatRoomResponseVo> getChatRoomsByUuid(String uuid) {
        log.info("memberUuid로 채팅방 리스트 찾기: {}", uuid);
        // uuid로 채팅방 목록 조회
        List<ChatRoom> chatRooms = chatRoomRepository.findAllByChatRoomMembers_MemberUuid(uuid);

        // vo에 담아서 반환
        List<ChatRoomResponseVo> chatRoomResponseVos = new ArrayList<>();
        for (ChatRoom chatRoom : chatRooms) {
            ChatRoomResponseVo chatRoomResponseVo = ChatRoomResponseVo.builder()
                .roomNumber(chatRoom.getRoomNumber())
                .title(chatRoom.getTitle())
                .thumbnail(chatRoom.getThumbnail())
                .updatedAt(chatRoom.getUpdatedAt())
                .build();
            chatRoomResponseVos.add(chatRoomResponseVo);
        }
        return chatRoomResponseVos;
    }

    @Override
    public PreviousChatResponseVo getPreviousChat(String roomNumber, LocalDateTime enterTime,
        int page, int size) {
        // 페이징에 담기
        Pageable pageable = PageRequest.of(page, size);
        Page<PreviousChatDto> previousChat = chatSyncRepository.findByRoomNumberAndCreatedAtBeforeOrderByCreatedAtDesc(
            roomNumber, enterTime, pageable);
        if (previousChat.getSize() == 0) {
            throw new CustomException(ResponseStatus.NO_DATA);
        }
        Optional<ChatRoom> optChatRoom = chatRoomRepository.findByRoomNumber(roomNumber);
        if (optChatRoom.isPresent()) {
            // 꺼내서 uuid로 handle과 profileImage넣기
            try {
                List<PreviousChatWithMemberInfoDto> previousChatWithMemberInfoDtos = previousChat.getContent()
                    .stream().map(chatDto -> {
                        Optional<ChatRoomMember> memberOpt = chatRoomMemberRepository.findByMemberUuidAndRoomNumber(
                            chatDto.getSenderUuid(), optChatRoom.get()
                                .getRoomNumber());
                        String handle = memberOpt.map(ChatRoomMember::getMemberHandle).orElse(null);
                        String profileImage = memberOpt.map(ChatRoomMember::getMemberProfileImage)
                            .orElse(null);

                        return PreviousChatWithMemberInfoDto.builder()
                            .uuid(chatDto.getSenderUuid())
                            .handle(handle)
                            .profileImage(profileImage)
                            .content(chatDto.getContent())
                            .createdAt(chatDto.getCreatedAt())
                            .build();
                    }).collect(Collectors.toList());

                boolean hasNext = previousChat.hasNext();
                return new PreviousChatResponseVo(previousChatWithMemberInfoDtos, page, hasNext);
            } catch (Exception e) {
                log.error("오류 발생 : {}", e.getMessage());
                throw new CustomException(ResponseStatus.MONGO_DB_ERROR);
            }
        } else {
            throw new CustomException(ResponseStatus.WRONG_CHATROOM_AND_MEMBER);
        }
    }

    @Override
    @Transactional
    public void leaveChatRoom(LeaveChatRoomDto leaveChatRoomDto) {
//        String redisKey = "room:" + leaveChatRoomDto.getRoomNumber() + ":member:" + leaveChatRoomDto.getUuid();
//        redisTemplate.delete(redisKey);
        mongoTemplate.updateFirst(
            Query.query(
                Criteria.where("memberUuid").is(leaveChatRoomDto.getUuid()).and("roomNumber")
                    .is(leaveChatRoomDto.getRoomNumber())),
            Update.update("lastReadTime", LocalDateTime.now()),
            ChatRoomMember.class
        );
        log.info("lastReadTime 수정 RoomNumber: {}, uuid: {}", leaveChatRoomDto.getRoomNumber(),
            leaveChatRoomDto.getUuid());
    }

    @Override
    public LastChatVo getLastChatSync(String uuid, String roomNumber) {
        // 첫 리스트 화면 출력을 위해 마지막 채팅 1개 들고익
        Optional<Chat> optionalChat = chatSyncRepository.findFirstByRoomNumberOrderByCreatedAtDesc(
            roomNumber);
        if (optionalChat.isPresent()) {
            return LastChatVo.builder()
                .content(optionalChat.get().getContent())
                .createdAt(optionalChat.get().getCreatedAt())
                .build();
        } else {
            throw new CustomException(ResponseStatus.NO_DATA);
        }
    }

    @Override
    public Flux<LastChatVo> getLastChat(String uuid, String roomNumber) {
        log.info("실시간 마지막 채팅 들고오기: {}", roomNumber);
        ChangeStreamOptions options = ChangeStreamOptions.builder()
            // DB의 insert를 감지
            .filter(Aggregation.newAggregation(
                Aggregation.match(
                    Criteria.where("operationType").is(OperationType.INSERT.getValue())),
                // roomNumber랑 일치하는지
                Aggregation.match(Criteria.where("fullDocument.roomNumber").is(roomNumber))
            ))
            .build();
        // 해당 변경 사항을 들고오기
        return reactiveMongoTemplate.changeStream("chat", options, Document.class)
            .map(ChangeStreamEvent::getBody)
            .map(document -> {
                log.info("검색: {}", document);
                return LastChatVo.builder()
                    .content(document.getString("content"))
                    .createdAt(LocalDateTime.ofInstant(document.getDate("createdAt").toInstant(),
                        ZoneId.systemDefault()))
                    .build();
            });
    }

    @Override
    public ChatRoomTitleResponseDto getChatRoomTitle(String uuid, String roomNumber) {
        ChatRoom chatRoom = chatRoomRepository.findByRoomNumberAndChatRoomMembers_MemberUuid(
                roomNumber, uuid)
            .orElseThrow(() -> new CustomException(ResponseStatus.WRONG_CHATROOM_AND_MEMBER));
        ChatRoomTitleResponseDto chatRoomTitleResponseDto = ChatRoomTitleResponseDto.builder()
            .title(chatRoom.getTitle())
            .build();
        return chatRoomTitleResponseDto;

    }

    @Override // 프로필 이미지 업데이트
    public void updateProfileImage(UpdateProfileImageRequestDto updateProfileImageRequestDto) {
        List<ChatRoomMember> chatRoomMembers =
            chatRoomMemberRepository.
                findAllByMemberUuid(updateProfileImageRequestDto.getMemberUuid());

        if (chatRoomMembers.isEmpty()) {
            throw new CustomException(ResponseStatus.NO_DATA);
        } else {

            try {
                mongoTemplate.updateMulti(
                    Query.query(
                        Criteria.where("memberUuid")
                            .is(updateProfileImageRequestDto.getMemberUuid())),
                    Update.update("memberProfileImage",
                        updateProfileImageRequestDto.getProfileImage()),
                    ChatRoomMember.class);
            } catch (Exception e) {
                log.error("오류 발생 : {}", e.getMessage());
                throw new CustomException(ResponseStatus.MONGO_DB_ERROR);
            }
        }
    }

    private void sendToAlarm(BeforeChatRoomDto beforeChatRoomDto, String roomNumber) {
//         log.info("alarmDto에 들어갈 receiverUuids: {}", beforeChatRoomDto.getMemberUuidsWithProfiles().keySet().toString());
//         log.info("alarmDto에 들어갈 message: {}", beforeChatRoomDto.getTitle());
        // 알람 서비스로 전송
        SendToAlarmDto sendToAlarmDto = SendToAlarmDto.builder()
            .eventType("chat")
            .roomNumber(roomNumber)
            .receiverUuids(
                new ArrayList<>(beforeChatRoomDto.getMemberUuidsWithProfiles().keySet()))
            .message(beforeChatRoomDto.getTitle() + " 채팅방이 열렸습니다.")
            .build();
        producer.sendMessage("alarm-topic", sendToAlarmDto);
        log.info("알람 서비스로 전송: {}", sendToAlarmDto.toString());
    }

    @Override
    public List<ChatRoomMemberResponseDto> getChatRoomMembers(String roomNumber) {
        try {
            // 조회
            List<ChatRoomMember> chatRoomMembers = chatRoomMemberRepository.findAllByRoomNumber(
                roomNumber);

            // Dto 변환
            return chatRoomMembers.stream()
                .map(chatRoomMember -> ChatRoomMemberResponseDto.builder()
                    .memberUuid(chatRoomMember.getMemberUuid())
                    .profileImage(chatRoomMember.getMemberProfileImage())
                    .handle(chatRoomMember.getMemberHandle())
                    .build())
                .toList();
        } catch (Exception e) {
            throw new CustomException(ResponseStatus.MONGO_DB_ERROR);
        }
    }

}