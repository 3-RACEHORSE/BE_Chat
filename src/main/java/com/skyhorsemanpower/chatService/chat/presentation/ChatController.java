package com.skyhorsemanpower.chatService.chat.presentation;

import com.skyhorsemanpower.chatService.chat.application.ChatService;
import com.skyhorsemanpower.chatService.chat.data.dto.ChatMemberDto;
import com.skyhorsemanpower.chatService.chat.data.dto.ChatRoomTitleResponseDto;
import com.skyhorsemanpower.chatService.chat.data.dto.LeaveChatRoomDto;
import com.skyhorsemanpower.chatService.chat.data.dto.SendChatRequestDto;
import com.skyhorsemanpower.chatService.chat.data.vo.AddChatRoomRequestVo;
import com.skyhorsemanpower.chatService.chat.data.vo.ChatRoomResponseVo;
import com.skyhorsemanpower.chatService.chat.data.vo.ChatRoomTitleResponseVo;
import com.skyhorsemanpower.chatService.chat.data.vo.ChatVo;
import com.skyhorsemanpower.chatService.chat.data.vo.GetChatVo;
import com.skyhorsemanpower.chatService.chat.data.vo.LastChatVo;
import com.skyhorsemanpower.chatService.chat.data.vo.LeaveChatRoomRequestVo;
import com.skyhorsemanpower.chatService.chat.data.vo.PreviousChatResponseVo;
import com.skyhorsemanpower.chatService.chat.data.vo.SendChatRequestVo;
import com.skyhorsemanpower.chatService.common.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/authorization/chat")
@CrossOrigin(origins = "*")
@Tag(name = "채팅", description = "채팅 관련 API")
@Slf4j
public class ChatController {
    private final ChatService chatService;
    private final Sinks.Many<ChatVo> sink = Sinks.many().multicast().onBackpressureBuffer();

    @PostMapping("/room")
    @Operation(summary = "채팅방 생성", description = "낙찰된 사용자와 판매자 사이의 채팅방을 생성")
    public SuccessResponse<Object> addChatRoom(@RequestBody AddChatRoomRequestVo addChatRoomRequestVo) {
        List<ChatMemberDto> chatMemberDtos = addChatRoomRequestVo.toChatMemberDto();
        chatService.createChatRoom(chatMemberDtos);
        return new SuccessResponse<>(null);
    }

    @PostMapping
    @Operation(summary = "채팅 메시지 전송", description = "채팅방 안에서 사용자가 채팅을 보내기")
    public SuccessResponse<Object> sendChat(@RequestBody SendChatRequestVo sendChatRequestVo,
        @RequestHeader String uuid) {
        SendChatRequestDto sendChatRequestDto = sendChatRequestVo.toSendChatRequestDto();
        chatService.sendChat(sendChatRequestDto, uuid);
        return new SuccessResponse<>(null);
    }

    @GetMapping("/chatRooms")
    @Operation(summary = "채팅방 리스트 조회", description = "웹소켓 방식으로 채팅방 리스트, 마지막 채팅을 조회")
    public List<ChatRoomResponseVo> getChatRooms(@RequestHeader String uuid) {
        return chatService.getChatRoomsByUuid(uuid);
    }

    @GetMapping(value = "/previous/{roomNumber}")
    @Operation(summary = "채팅방 이전 메시지 조회", description = "채팅방에서 이전 메시지를 조회")
    public SuccessResponse<PreviousChatResponseVo> getPreviousChat(
        @PathVariable(value = "roomNumber") String roomNumber,
        @RequestParam LocalDateTime enterTime,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "40") int size) {
        log.info("roomNumber: {}", roomNumber);
        PreviousChatResponseVo previousChatResponseVo = chatService.getPreviousChat(roomNumber, enterTime, page, size);
        return new SuccessResponse<>(previousChatResponseVo);
    }

    @GetMapping(value = "/roomNumber/{roomNumber}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "채팅방 메시지 조회", description = "채팅방에서 전체 메시지를 조회")
    public Flux<GetChatVo> getChat(@PathVariable(value = "roomNumber") String roomNumber,
        @RequestHeader String uuid) {
        log.info("getChat 실행: roomNumber={}, uuid={}", roomNumber, uuid);
        return chatService.getChat(roomNumber, uuid);
    }

    @PutMapping("/leaveChatRoom")
    @Operation(summary = "입장정보 삭제", description = "검색한 곳에서 상태가 바뀌면 beforeUnload를 실행시키고\n\n"
        + "performance.navigation.type이 1(새로고침)인 경우를 제외하면 된다고 함")
    public SuccessResponse<Object> leaveChatRoom(@RequestBody LeaveChatRoomRequestVo leaveChatRoomRequestVo) {
        LeaveChatRoomDto leaveChatRoomDto = leaveChatRoomRequestVo.toLeaveChatRoomDto();
        chatService.leaveChatRoom(leaveChatRoomDto);
        return new SuccessResponse<>(null);
    }

    @GetMapping(value = "/roomNumber/{roomNumber}/last")
    @Operation(summary = "채팅 리스트 불러올 때, 마지막 채팅", description = "첫 채팅 리스트를 불러올 때 마지막 채팅 조회")
    public SuccessResponse<LastChatVo> lastChatSync(@PathVariable(value = "roomNumber") String roomNumber,
        @RequestHeader String uuid) {
        LastChatVo lastChatVo = chatService.getLastChatSync(uuid, roomNumber);
        return new SuccessResponse<>(lastChatVo);
    }

    @GetMapping(value = "/roomNumber/{roomNumber}/new", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "계속 바뀌는 마지막 채팅", description = "계속 변화를 감지해 변하는 마지막 채팅")
    public SuccessResponse<Flux<LastChatVo>> lastChat(@PathVariable(value = "roomNumber") String roomNumber,
        @RequestHeader String uuid) {
        Flux<LastChatVo> lastChatVo = chatService.getLastChat(uuid, roomNumber);
        return new SuccessResponse<>(lastChatVo);
    }
    @GetMapping(value = "/roomNumber/{roomNumber}/title")
    @Operation(summary = "채팅방 제목", description = "채팅방 상단의 제목")
    public SuccessResponse<ChatRoomTitleResponseVo> chatRoomTitle(@PathVariable(value = "roomNumber") String roomNumber,
        @RequestHeader String uuid) {
        return new SuccessResponse<>(
            ChatRoomTitleResponseDto.dtoToVo(chatService.getChatRoomTitle(uuid, roomNumber)));
    }
}
