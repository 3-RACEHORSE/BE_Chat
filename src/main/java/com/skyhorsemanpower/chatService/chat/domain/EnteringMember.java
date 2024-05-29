package com.skyhorsemanpower.chatService.chat.domain;

import com.skyhorsemanpower.chatService.common.CommonCreateTime;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@RedisHash(value = "entering member", timeToLive = 10)
@Getter
public class EnteringMember {
    @Id
    private String id;

    private String uuid;
    private String roomNumber;
    private LocalDateTime enterTime;

    @Builder
    public EnteringMember(String uuid, String roomNumber) {
        this.uuid = uuid;
        this.roomNumber = roomNumber;
    }
}
