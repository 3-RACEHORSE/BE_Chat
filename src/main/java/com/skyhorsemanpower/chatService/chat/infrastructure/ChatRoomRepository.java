package com.skyhorsemanpower.chatService.chat.infrastructure;

import com.mongodb.RequestContext;
import com.skyhorsemanpower.chatService.chat.domain.ChatRoom;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    Optional<ChatRoom> findByRoomNumber(String roomNumber);
    List<ChatRoom> findByMemberUuidsContaining(String memberUuid);
    Optional<ChatRoom> findByMemberUuidsContainingAndRoomNumber(String memberUuid,String roomNumber);
}
