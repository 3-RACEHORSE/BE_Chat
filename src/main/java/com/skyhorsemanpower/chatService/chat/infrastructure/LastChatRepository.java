package com.skyhorsemanpower.chatService.chat.infrastructure;

import com.skyhorsemanpower.chatService.chat.domain.LastChat;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface LastChatRepository extends MongoRepository<LastChat, String> {

    Optional<LastChat> findFirstByRoomNumberOrderByLastChatTimeDesc(String roomNumber);
}
