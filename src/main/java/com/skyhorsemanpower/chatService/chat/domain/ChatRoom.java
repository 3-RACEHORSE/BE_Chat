package com.skyhorsemanpower.chatService.chat.domain;

import com.skyhorsemanpower.chatService.common.CommonCreateTime;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
@Getter
public class ChatRoom extends CommonCreateTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String memberUuid;

    private String roomNumber; // 채팅방 번호는 부족할수도 있어서 랜덤으로 조합해서 사용(String)

    @Builder
    public ChatRoom(Long id, String memberUuid, String roomNumber) {
        this.id = id;
        this.memberUuid = memberUuid;
        this.roomNumber = roomNumber;
    }
}
