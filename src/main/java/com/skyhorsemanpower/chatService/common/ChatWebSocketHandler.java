package com.skyhorsemanpower.chatService.common;

import com.skyhorsemanpower.chatService.chat.application.ChatService;
import com.skyhorsemanpower.chatService.chat.data.dto.ChatRoomListElementDto;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final ChatService chatService;

    @Autowired
    public ChatWebSocketHandler(ChatService chatService) {
        this.chatService = chatService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 연결이 설정되면 해당 세션에 대한 처리를 수행합니다.
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 클라이언트로부터 메시지를 수신하면 처리합니다.
    }

    // 채팅 목록을 클라이언트에게 전송하는 메서드
    public void sendChatRoomsUpdate(String userUuid) {
        List<ChatRoomListElementDto> chatRooms = chatService.getChatRoomsByUserUuid(userUuid).collectList().block();
        // 채팅 목록을 클라이언트로 전송합니다.
    }
}

