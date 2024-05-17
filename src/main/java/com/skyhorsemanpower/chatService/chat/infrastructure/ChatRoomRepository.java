package com.skyhorsemanpower.chatService.chat.infrastructure;

import com.skyhorsemanpower.chatService.chat.domain.ChatRoom;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    List<ChatRoom> findAllByMemberUuid(String memberUuid);
}
