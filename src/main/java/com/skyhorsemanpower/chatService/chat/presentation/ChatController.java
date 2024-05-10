package com.skyhorsemanpower.chatService.chat.presentation;

import com.skyhorsemanpower.chatService.chat.application.ChatService;
import com.skyhorsemanpower.chatService.chat.data.dto.ChatMemberDto;
import com.skyhorsemanpower.chatService.chat.data.vo.AddChatRoomRequestVo;
import lombok.RequiredArgsConstructor;
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
    public String addChatRoom(@RequestBody AddChatRoomRequestVo addChatRoomRequestVo) {
        ChatMemberDto chatMemberDto = addChatRoomRequestVo.toChatMemberDto();
        if (chatService.createChatRoom(chatMemberDto)) {
            return "성공";
        } else {
            return "실패";
        }
    }
}
