package com.skyhorsemanpower.chatService.common.webSocket;

import com.skyhorsemanpower.chatService.chat.application.ChatService;
import com.skyhorsemanpower.chatService.chat.domain.Chat;
import java.io.IOException;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import reactor.core.publisher.Flux;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final ChatService chatService;

    public ChatWebSocketHandler(ChatService chatService) {
        this.chatService = chatService;
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 클라이언트로부터 메시지를 받으면 처리
        String roomId = message.getPayload(); // 클라이언트에서 받은 메시지를 방 ID로 사용

        Flux<Chat> lastChatInRoom = chatService.getLastChatInRoom(roomId);

        lastChatInRoom.subscribe(
            chat -> {
                try {
                    // 채팅방의 마지막 채팅을 클라이언트에게 전송
                    session.sendMessage(new TextMessage(chat.getContent()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        );
    }
}