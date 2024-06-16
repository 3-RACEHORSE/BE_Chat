package com.skyhorsemanpower.chatService.chat.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.model.changestream.OperationType;
import com.skyhorsemanpower.chatService.chat.data.dto.BeforeChatRoomDto;
import com.skyhorsemanpower.chatService.chat.data.dto.ChatMemberDto;
import com.skyhorsemanpower.chatService.chat.data.dto.EnteringMemberDto;
import com.skyhorsemanpower.chatService.chat.data.dto.LeaveChatRoomDto;
import com.skyhorsemanpower.chatService.chat.data.dto.PreviousChatDto;
import com.skyhorsemanpower.chatService.chat.data.dto.PreviousChatWithMemberInfoDto;
import com.skyhorsemanpower.chatService.chat.data.dto.SendChatRequestDto;
import com.skyhorsemanpower.chatService.chat.data.vo.AuctionInfoResponseVo;
import com.skyhorsemanpower.chatService.chat.data.vo.ChatRoomResponseVo;
import com.skyhorsemanpower.chatService.chat.data.vo.ChatVo;
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
import reactor.core.publisher.Sinks;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatServiceImp implements ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRepository chatRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final ChatSyncRepository chatSyncRepository;
    private final Sinks.Many<ChatVo> sink = Sinks.many().multicast().onBackpressureBuffer();
    private final RedisTemplate<String, String> redisTemplate;
    private final ReactiveMongoTemplate reactiveMongoTemplate;
    private final MongoTemplate mongoTemplate;
    private final AuctionPostClient auctionPostClient;

    @Transactional
    @Override
    public void createChatRoom(List<ChatMemberDto> chatMemberDtos) {

//        if (chatMemberDtos.size() < 2) {
//            throw new CustomException(ResponseStatus.NOT_ENOUGH_MEMBERS);
//        }

        try {
            String roomNumber = UUID.randomUUID().toString();

            // 채팅방 회원 만들기
            List<ChatRoomMember> chatRoomMembers = chatMemberDtos.stream().map(dto -> {
                // 관형사 + 동물 이름 조합으로 랜덤 핸들 생성
                String handle = RandomHandleGenerator.generateRandomWord();
                String profile = RandomHandleGenerator.randomProfile();
                // Todo 관리자 전용 프로필과 handle을 만들어야함 관리자 판별 기준은 uuid앞에 admin 추가
                return ChatRoomMember.builder()
                    .memberUuid(dto.getMemberUuid())
                    .memberHandle(handle)
                    // 랜덤 프로필 생성 이미지
                    .memberProfileImage(profile)
                    .roomNumber(roomNumber)
                    .lastReadTime(LocalDateTime.now())
                    .build();
            }).collect(Collectors.toList());

            // 채팅방 저장
            ChatRoom chatRoom = ChatRoom.builder()
                .roomNumber(roomNumber)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .chatRoomMembers(chatRoomMembers)
                .title("*** 공지방")
                .thumbnail("https://ifh.cc/g/a3lcrS.png")
                .build();

            chatRoomRepository.save(chatRoom);
            log.info("ChatRoom 저장완료: {}", roomNumber);

            // 채팅방 회원 저장
            chatRoomMembers.forEach(chatRoomMember -> {
                chatRoomMemberRepository.save(chatRoomMember);
                log.info("chatRoomMember 저장완료: {}", chatRoomMember);
            });

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

        // 채팅 저장
        saveChatMessage(sendChatRequestDto, uuid);
    }

    private void verifyChatRoomAndMemberExistence(SendChatRequestDto sendChatRequestDto, String uuid) {
        // chatRoom에 조회
        boolean isMemberInChatRoom = chatRoomMemberRepository.findByMemberUuidAndRoomNumber(
            uuid, sendChatRequestDto.getRoomNumber()).isPresent();
        log.info("isMemberInChatRoom : {}",isMemberInChatRoom);
        if (!isMemberInChatRoom) {
            throw new CustomException(ResponseStatus.WRONG_CHATROOM_AND_MEMBER);
        }
    }

    @Transactional
    protected void saveChatMessage(SendChatRequestDto sendChatRequestDto, String uuid) {
        log.info("saveChatMessage 시작");
        Chat chat = Chat.builder()
            .senderUuid(uuid)
            .content(sendChatRequestDto.getContent())
            .roomNumber(sendChatRequestDto.getRoomNumber())
            .createdAt(LocalDateTime.now())
            .build();
        chatRepository.save(chat).subscribe();
    }

    @Override
    public Flux<GetChatVo> getChat(String roomNumber, String uuid) {
        ChatRoomMember chatRoomMember = chatRoomMemberRepository.findByMemberUuidAndRoomNumber(
            uuid, roomNumber).orElseThrow(() -> new CustomException(ResponseStatus.WRONG_CHATROOM_AND_MEMBER));

        LocalDateTime now = LocalDateTime.now();

        return chatRepository.findChatByRoomNumberAndCreatedAtOrAfterOrdOrderByCreatedAtDesc(
                roomNumber, now)
            .flatMap(chatVo -> {
                String senderUuid = chatVo.getSenderUuid();
                return Mono.justOrEmpty(chatRoomMemberRepository.findByMemberUuidAndRoomNumber(senderUuid, roomNumber))
                    .switchIfEmpty(Mono.error(new CustomException(ResponseStatus.WRONG_CHATROOM_AND_MEMBER)))
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
        if (chatRooms.isEmpty()) {
            throw new CustomException(ResponseStatus.NO_DATA);
        }
        // vo에 담아서 반환
        List<ChatRoomResponseVo> chatRoomResponseVos = new ArrayList<>();
        for(ChatRoom chatRoom : chatRooms) {
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
    public PreviousChatResponseVo getPreviousChat(String roomNumber, LocalDateTime enterTime, int page, int size) {
        // 페이징에 담기
        Pageable pageable = PageRequest.of(page, size);
        Page<PreviousChatDto> previousChat = chatSyncRepository.findByRoomNumberAndCreatedAtBeforeOrderByCreatedAtDesc(roomNumber, enterTime, pageable);
        if(previousChat.getSize() == 0){
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
//    @Override
//    public void enteringMember(String uuid, String roomNumber) {
//        String otherUuid = findOtherMemberUuid(uuid, roomNumber);
//        log.info("otherUuid : {}", otherUuid);
//        log.info("enteringMember 실행: roomNumber={}, uuid={}", roomNumber, uuid);
//        try {
//            // 데이터를 JSON 형식으로 변환
//            ObjectMapper objectMapper = new ObjectMapper();
//            EnteringMemberDto enteringMemberDto =
//                EnteringMemberDto.builder()
//                    .roomNumber(roomNumber)
//                    .uuid(uuid)
//                    .build();
//            String jsonData = objectMapper.writeValueAsString(enteringMemberDto);
//
//            // Redis에 데이터 저장
//            String redisKey = "room:" + roomNumber + ":member:" + uuid;
//            redisTemplate.opsForValue().set(redisKey, jsonData);
//
//        } catch (Exception e) {
//            log.error("Redis에 데이터 저장 중 오류 발생", e);
//            throw new CustomException(ResponseStatus.REDIS_DB_ERROR);
//        }
//    }
//    @Override
//    public String findOtherMemberUuid(String uuid, String roomNumber) {
//        log.info("findOtherMemberUuid 시작: uuid={}, roomNumber={}", uuid, roomNumber);
//
//        List<ChatRoomMember> chatRoomMembers = chatRoomMemberRepository.findAllByRoomNumber(roomNumber);
//
//        if (chatRoomMembers.isEmpty()) {
//            log.error("올바르지 않은 채팅방입니다. roomNumber={}, uuid={}", roomNumber, uuid);
//            throw new CustomException(ResponseStatus.WRONG_CHATROOM_AND_MEMBER);
//        }
//
//        for (ChatRoomMember member : chatRoomMembers) {
//            if (!member.getMemberUuid().equals(uuid)) {
//                log.info("다른 멤버 UUID 찾음: {}", member.getMemberUuid());
//                return member.getMemberUuid();
//            }
//        }
//
//        log.error("UUID가 채팅방에 포함되어 있지 않음: roomNumber={}, uuid={}", roomNumber, uuid);
//        throw new CustomException(ResponseStatus.WRONG_CHATROOM_AND_MEMBER);
//    }

//    private boolean isMemberDataExists(String uuid, String roomNumber) {
//        try {
//            String redisKey = "room:" + roomNumber + ":member:" + uuid;
//            Boolean exists = redisTemplate.hasKey(redisKey);
//
//            if (exists != null && exists) {
//                log.info("Redis에 데이터가 존재합니다: roomNumber={}, uuid={}", roomNumber, uuid);
//                return true;
//            } else {
//                log.info("Redis에 데이터가 존재하지 않습니다: roomNumber={}, uuid={}", roomNumber, uuid);
//                return false;
//            }
//        } catch (Exception e) {
//            log.error("Redis에서 데이터 존재 여부 확인 중 오류 발생", e);
//            throw new CustomException(ResponseStatus.REDIS_DB_ERROR);
//        }
//    }

//    @Override
//    public int getUnreadChatCount(String roomNumber, String uuid) {
//        String otherUuid = findOtherMemberUuid(uuid, roomNumber);
//        int readCount = 1;
//        try {
//            List<Chat> chats = chatSyncRepository.findAllByRoomNumberAndSenderUuidAndReadCount(
//                roomNumber, otherUuid, readCount);
//            return chats.size();
//        } catch (Exception e) {
//            throw new CustomException(ResponseStatus.MONGO_DB_ERROR);
//        }
//    }

    @Override
    @Transactional
    public void leaveChatRoom(LeaveChatRoomDto leaveChatRoomDto) {
//        String redisKey = "room:" + leaveChatRoomDto.getRoomNumber() + ":member:" + leaveChatRoomDto.getUuid();
//        redisTemplate.delete(redisKey);
        mongoTemplate.updateFirst(
            Query.query(Criteria.where("memberUuid").is(leaveChatRoomDto.getUuid()).and("roomNumber").is(leaveChatRoomDto.getRoomNumber())),
            Update.update("lastReadTime", LocalDateTime.now()),
            ChatRoomMember.class
        );
        log.info("lastReadTime 수정 RoomNumber: {}, uuid: {}", leaveChatRoomDto.getRoomNumber(), leaveChatRoomDto.getUuid());
    }
    @Override
    public LastChatVo getLastChatSync(String uuid, String roomNumber) {
        // 첫 리스트 화면 출력을 위해 마지막 채팅 1개 들고익
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
    @Override
    public Flux<LastChatVo> getLastChat(String uuid, String roomNumber) {
        log.info("실시간 마지막 채팅 들고오기: {}", roomNumber);
        ChangeStreamOptions options = ChangeStreamOptions.builder()
            // DB의 insert를 감지
            .filter(Aggregation.newAggregation(
                Aggregation.match(Criteria.where("operationType").is(OperationType.INSERT.getValue())),
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
                    .createdAt(LocalDateTime.ofInstant(document.getDate("createdAt").toInstant(), ZoneId.systemDefault()))
                    .build();
            });
    }

    @Override
    public void convertToChatRoomData(BeforeChatRoomDto beforeChatRoomDto) {
        AuctionInfoResponseVo auctionInfoResponseVo = auctionPostClient.getAuctionInfo(beforeChatRoomDto.getAuctionUuid());
        log.info(auctionInfoResponseVo.toString());
    }

    @Override
    public void test() {
        String auctionUuid = "202406120010-ce8888c96d";
        AuctionInfoResponseVo auctionInfoResponseVo = auctionPostClient.getAuctionInfo(auctionUuid);
        log.info(auctionInfoResponseVo.toString());
    }
}