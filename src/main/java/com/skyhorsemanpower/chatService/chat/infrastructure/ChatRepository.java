package com.skyhorsemanpower.chatService.chat.infrastructure;

import com.skyhorsemanpower.chatService.chat.data.vo.ChatVo;
import com.skyhorsemanpower.chatService.chat.domain.Chat;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Tailable;
import reactor.core.publisher.Flux;

public interface ChatRepository extends ReactiveMongoRepository<Chat, String> {
    @Tailable
    @Query("{ 'roomNumber' : ?0}")
    Flux<ChatVo> findChatByRoomNumber(String roomNumber);

    Flux<Chat> findLastChatByRoomNumber(String roomNumber);
}
