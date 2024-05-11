package com.skyhorsemanpower.chatService.chat.presentation;

import com.skyhorsemanpower.chatService.chat.application.ChatService;
import com.skyhorsemanpower.chatService.chat.data.dto.ChatMemberDto;
import com.skyhorsemanpower.chatService.chat.data.vo.AddChatRoomRequestVo;
import com.skyhorsemanpower.chatService.common.ExceptionResponse;
import com.skyhorsemanpower.chatService.common.ResponseStatus;
import com.skyhorsemanpower.chatService.common.SuccessResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/chat")
public class ChatController {
    private final ChatService chatService;

    @PostMapping("/room")
    public ResponseEntity<?> addChatRoom(@RequestBody AddChatRoomRequestVo addChatRoomRequestVo) {
        List<ChatMemberDto> chatMemberDtos = addChatRoomRequestVo.toChatMemberDto();
        boolean result = chatService.createChatRoom(chatMemberDtos);
        if (result) {
            return new SuccessResponse<>("요청 성공");
        } else {
            return new ExceptionResponse(ResponseStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
