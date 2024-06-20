package com.skyhorsemanpower.chatService.chat.infrastructure;

import com.skyhorsemanpower.chatService.chat.domain.ChatRoom;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomRepository extends MongoRepository<ChatRoom, String> {
    List<ChatRoom> findAllByChatRoomMembers_MemberUuid(String uuid);
    Optional<ChatRoom> findByRoomNumber(String roomNumber);
    Optional<ChatRoom> findByRoomNumberAndChatRoomMembers_MemberUuid(String roomNumber, String memberUuid);
    List<ChatRoom> findAll();

}
