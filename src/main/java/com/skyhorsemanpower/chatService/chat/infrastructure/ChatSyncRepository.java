package com.skyhorsemanpower.chatService.chat.infrastructure;

import com.skyhorsemanpower.chatService.chat.data.dto.PreviousChatDto;
import com.skyhorsemanpower.chatService.chat.data.vo.ChatVo;
import com.skyhorsemanpower.chatService.chat.domain.Chat;
import com.skyhorsemanpower.chatService.chat.domain.LastChat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatSyncRepository extends MongoRepository<Chat, String> {
    Page<PreviousChatDto> findByRoomNumberAndCreatedAtBeforeOrderByCreatedAtDesc(String roomNumber, LocalDateTime enterTime, Pageable pageable);
    List<Chat> findAllByRoomNumberAndSenderUuidAndReadCount(String roomNumber, String senderUuid, int readCount);
    Optional<Chat> findFirstByRoomNumberOrderByCreatedAtDesc(String roomNumber);
}
