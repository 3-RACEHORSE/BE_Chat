package com.skyhorsemanpower.chatService.chat.data.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SendToAlarmDto {

    private List<String> receiverUuids;
    private String eventType;
    private String message;
    private String roomNumber;
}
