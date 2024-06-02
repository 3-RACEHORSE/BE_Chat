package com.skyhorsemanpower.chatService.chat.infrastructure;

import com.skyhorsemanpower.chatService.chat.data.vo.ChatVo;
import com.skyhorsemanpower.chatService.chat.domain.Chat;
import java.time.LocalDateTime;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Tailable;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
@Repository
public interface ChatRepository extends ReactiveMongoRepository<Chat, String> {
    @Tailable
    @Query("{ 'roomNumber' : ?0 }")
    Flux<ChatVo> findChatByRoomNumber(String roomNumber);
    @Tailable
    @Query(value = "{ 'roomNumber' : ?0, 'createdAt' : { $gte: ?1 } }", fields = "{ 'id': 0, 'roomNumber': 0 }")
    Flux<ChatVo> findChatByRoomNumberAndCreatedAtOrAfter(String roomNumber, LocalDateTime now);
}
