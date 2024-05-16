package com.skyhorsemanpower.chatService.chat.presentation;

import com.skyhorsemanpower.chatService.chat.application.ChatService;
import com.skyhorsemanpower.chatService.chat.data.dto.ChatMemberDto;
import com.skyhorsemanpower.chatService.chat.data.dto.ChatRoomWithLastChatDto;
import com.skyhorsemanpower.chatService.chat.data.vo.AddChatRoomRequestVo;
import com.skyhorsemanpower.chatService.chat.data.vo.ChatVo;
import com.skyhorsemanpower.chatService.common.ExceptionResponse;
import com.skyhorsemanpower.chatService.common.ResponseStatus;
import com.skyhorsemanpower.chatService.common.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/chat")
@Tag(name = "채팅", description = "채팅 관련 API")
@Slf4j
public class ChatController {
    private final ChatService chatService;

    @PostMapping("/room")
    @Operation(summary = "채팅방 생성", description = "낙찰된 사용자와 판매자 사이의 채팅방을 생성")
    public ResponseEntity<?> addChatRoom(@RequestBody AddChatRoomRequestVo addChatRoomRequestVo) {
        List<ChatMemberDto> chatMemberDtos = addChatRoomRequestVo.toChatMemberDto();
        boolean result = chatService.createChatRoom(chatMemberDtos);
        if (result) {
            return new SuccessResponse<>("요청 성공");
        } else {
            return new ExceptionResponse(ResponseStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    @Operation(summary = "채팅 메시지 전송", description = "채팅방 안에서 사용자가 채팅을 보내기")
    public SuccessResponse<Object> sendChat(@RequestBody ChatVo chatvo) {
        log.info("chatVo: {}", chatvo);
        chatService.sendChat(chatvo);
        return new SuccessResponse<>(null);
    }

    @GetMapping(value = "/roomNumber/{roomNumber}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "채팅방 메시지 조회", description = "채팅방에서 전체 메시지를 조회")
    public SuccessResponse<Flux<ChatVo>> getChat(
        @PathVariable(value = "roomNumber") String roomNumber) {
        log.info("roomNumber: {}", roomNumber);
        Flux<ChatVo> chatVo = chatService.getChat(roomNumber);
        return new SuccessResponse<>(chatVo);
    }

    @GetMapping("/rooms")
    @Operation(summary = "채팅방 리스트 조회", description = "채팅방, 마지막 채팅, 마지막 채팅 시간을 조회")
    public SuccessResponse<List<ChatRoomWithLastChatDto>> getAllChatRooms(@RequestParam String memberUuid) {
        // 사용자의 채팅방 목록을 가져와서 각 채팅방의 마지막 채팅을 포함하여 반환
        List<ChatRoomWithLastChatDto> chatRoomsWithLastChatDto = chatService.getAllChatRoomsWithLastChat(memberUuid);
        return new SuccessResponse<>(chatRoomsWithLastChatDto);
    }

    // WebSocket 핸들러
    @MessageMapping("/new-chat")
    public void handleNewChat(ChatVo chatVo) {
        // 새로운 채팅을 받았을 때 처리 로직
        chatService.sendChat(chatVo);
    }
}
