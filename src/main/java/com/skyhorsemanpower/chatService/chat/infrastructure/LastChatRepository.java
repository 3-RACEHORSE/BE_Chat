package com.skyhorsemanpower.chatService.chat.infrastructure;

import com.skyhorsemanpower.chatService.chat.domain.LastChat;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LastChatRepository extends MongoRepository<LastChat, String> {

}
