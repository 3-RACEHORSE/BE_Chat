package com.skyhorsemanpower.chatService.chat.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skyhorsemanpower.chatService.chat.data.dto.ChatMemberDto;
import com.skyhorsemanpower.chatService.chat.data.dto.ChatRoomListElementDto;
import com.skyhorsemanpower.chatService.chat.data.dto.EnteringMemberDto;
import com.skyhorsemanpower.chatService.chat.data.dto.LeaveChatRoomDto;
import com.skyhorsemanpower.chatService.chat.data.dto.PreviousChatDto;
import com.skyhorsemanpower.chatService.chat.data.dto.PreviousChatWithMemberInfoDto;
import com.skyhorsemanpower.chatService.chat.data.dto.SendChatRequestDto;
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
import com.skyhorsemanpower.chatService.common.RandomHandleGenerator;
import com.skyhorsemanpower.chatService.common.response.CustomException;
import com.skyhorsemanpower.chatService.common.response.ResponseStatus;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
import org.springframework.stereotype.Service;
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
    private final MongoTemplate mongoTemplate;

    @Override
    public void createChatRoom(List<ChatMemberDto> chatMemberDtos) {

        if (chatMemberDtos.size() < 2) {
            throw new CustomException(ResponseStatus.NOT_ENOUGH_MEMBERS);
        }

        try {
            String roomNumber = UUID.randomUUID().toString();
            ChatRoom chatRoom = ChatRoom.builder()
                .roomNumber(roomNumber)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
            chatRoomRepository.save(chatRoom);
            log.info("ChatRoom 저장완료: {}", roomNumber);

            chatMemberDtos.forEach(dto -> {
                log.info("member서비스에서 uuid로 조회하기: {}", dto.getMemberUuid());
                // 관형사 + 동물 이름 조합으로 랜덤 핸들 생성
                String handle = RandomHandleGenerator.generateRandomWord();
                String profile = RandomHandleGenerator.randomProfile();
                ChatRoomMember chatRoomMember = ChatRoomMember.builder()
                    .memberUuid(dto.getMemberUuid())
                    .memberHandle(handle)
                    // 랜덤 프로필 생성 이미지
                    .memberProfileImage(profile)
                    .roomNumber(roomNumber)
                    .build();
                chatRoomMemberRepository.save(chatRoomMember);
                log.info("chatRoomMember 저장완료: {}", chatRoomMember);
            });

        } catch (Exception e) {
            log.error("채팅 방 생성중 오류 발생: {}", e.getMessage());
            throw new CustomException(ResponseStatus.CREATE_CHATROOM_FAILED);
        }
    }

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

    private void saveChatMessage(SendChatRequestDto sendChatRequestDto, String uuid) {
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
        enteringMember(uuid, roomNumber);

        ChatRoomMember chatRoomMember = chatRoomMemberRepository.findByMemberUuidAndRoomNumber(
            uuid, roomNumber).orElseThrow(() -> new CustomException(ResponseStatus.WRONG_CHATROOM_AND_MEMBER));

        LocalDateTime now = LocalDateTime.now();
        String handle = chatRoomMember.getMemberHandle();
        String profileImage = chatRoomMember.getMemberProfileImage();

        return chatRepository.findChatByRoomNumberAndCreatedAtOrAfterOrdOrderByCreatedAtDesc(
                roomNumber, now)
            .flatMap(chatVo -> {
                GetChatVo getChatVo = GetChatVo.builder()
                    .handle(handle)
                    .profileImage(profileImage)
                    .content(chatVo.getContent())
                    .createdAt(chatVo.getCreatedAt())
                    .build();
                return Mono.just(getChatVo);
                });
    }

    @Override
    public List<ChatRoomListElementDto> getChatRoomsByUuid(String uuid) {
        // Todo 1:1 채팅을 가정해서 상대방의 핸들과 프로필 사진을 띄우게 했는데 이제는 그룹채팅도 있어서 수정이 필요함
        // uuid로 채팅방 목록 조회
        List<ChatRoom> chatRooms = chatRoomRepository.findAllByChatRoomMembers_MemberUuid(uuid);

        List<ChatRoomListElementDto> chatRoomList = new ArrayList<>();
        for (ChatRoom chatRoom : chatRooms) {
            // 각 채팅방 별 마지막 메시지
            Optional<Chat> chat = chatSyncRepository.findFirstByRoomNumberOrderByCreatedAtDesc(chatRoom.getRoomNumber());

            String otherUuid = findOtherMemberUuid(uuid, chatRoom.getRoomNumber());
            ChatRoomMember chatRoomMember = chatRoomMemberRepository.findByMemberUuidAndRoomNumber(otherUuid,
                chatRoom.getRoomNumber()).orElseThrow(() -> new CustomException(ResponseStatus.WRONG_CHATROOM_AND_MEMBER));

            String handle = chatRoomMember.getMemberHandle();
            String profileImage = chatRoomMember.getMemberProfileImage();
            // 마지막 채팅이 있는 것과 없는 것 구분해서 넣기
            ChatRoomListElementDto chatRoomElement = chat.map(ch -> ChatRoomListElementDto.builder()
                    .roomNumber(chatRoom.getRoomNumber())
                    .lastChat(ch.getContent())
                    .lastChatTime(ch.getCreatedAt())
                    .memberUuid(otherUuid)
                    .handle(handle)
                    .profileImage(profileImage)
                    .build())
                .orElseGet(() -> ChatRoomListElementDto.builder()
                    .roomNumber(chatRoom.getRoomNumber())
                    .lastChat(null)
                    .lastChatTime(null)
                    .memberUuid(otherUuid)
                    .handle(handle)
                    .profileImage(profileImage)
                    .build());
            chatRoomList.add(chatRoomElement);
            }
        return chatRoomList;
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
                            .readCount(chatDto.getReadCount())
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

        List<ChatRoomMember> chatRoomMembers = chatRoomMemberRepository.findAllByRoomNumber(roomNumber);

        if (chatRoomMembers.isEmpty()) {
            log.error("올바르지 않은 채팅방입니다. roomNumber={}, uuid={}", roomNumber, uuid);
            throw new CustomException(ResponseStatus.WRONG_CHATROOM_AND_MEMBER);
        }

        for (ChatRoomMember member : chatRoomMembers) {
            if (!member.getMemberUuid().equals(uuid)) {
                log.info("다른 멤버 UUID 찾음: {}", member.getMemberUuid());
                return member.getMemberUuid();
            }
        }

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
}