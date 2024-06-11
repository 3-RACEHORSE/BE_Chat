package com.skyhorsemanpower.chatService.chat.infrastructure;

import com.skyhorsemanpower.chatService.chat.domain.ChatRoom;
import com.skyhorsemanpower.chatService.chat.domain.ChatRoomMember;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatRoomMemberRepository extends MongoRepository<ChatRoomMember, String> {
    Optional<ChatRoomMember> findByMemberUuidAndRoomNumber(String uuid, String roomNumber);
}
