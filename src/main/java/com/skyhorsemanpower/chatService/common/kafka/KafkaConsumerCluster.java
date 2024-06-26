package com.skyhorsemanpower.chatService.common.kafka;
import com.skyhorsemanpower.chatService.chat.application.ChatService;
import com.skyhorsemanpower.chatService.chat.data.vo.BeforeChatRoomVo;
import com.skyhorsemanpower.chatService.chat.data.vo.UpdateProfileImageRequestVo;
import com.skyhorsemanpower.chatService.common.kafka.Topics.Constant;
import java.util.LinkedHashMap;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaConsumerCluster {

    private final ChatService chatService;

    @KafkaListener(topics = Constant.SEND_TO_CHAT)
    public void consumePayment(@Payload LinkedHashMap<String, Object> message,
        @Headers MessageHeaders messageHeaders) {
        log.info("consumer: success >>> message: {}, headers: {}", message.toString(),
            messageHeaders);
        //message를 PaymentReadyVo로 변환
        BeforeChatRoomVo beforeChatRoomVo = BeforeChatRoomVo.builder()
            .auctionUuid(message.get("auctionUuid").toString())
            .title(message.get("title").toString())
            .thumbnail(message.get("thumbnail").toString())
            .memberUuids((List<String>) message.get("memberUuids"))
            .build();
        log.info("auctionUuid : {}", beforeChatRoomVo.getAuctionUuid());
        log.info("title : {}", beforeChatRoomVo.getTitle());
        log.info("thumbnail : {}", beforeChatRoomVo.getThumbnail());
        log.info("memberUuids : {}", beforeChatRoomVo.getMemberUuids());
        chatService.createChatRoom(beforeChatRoomVo.toBeforeChatRoomDto());
    }

    @KafkaListener(topics = Constant.CHANGE_PROFILE_IMAGE)
    public void consumeMember(@Payload LinkedHashMap<String, Object> message,
        @Headers MessageHeaders messageHeaders) {
        log.info("consumer: success >>> message: {}, headers: {}", message.toString(),
            messageHeaders);
        //message를 Chan로 변환
        UpdateProfileImageRequestVo updateProfileImageRequestVo = UpdateProfileImageRequestVo.builder()
            .memberUuid(message.get("memberUuid").toString())
            .profileImage(message.get("profileImage").toString())
            .build();
        log.info("memberUuid : {}", updateProfileImageRequestVo.getMemberUuid());
        log.info("profileImage : {}", updateProfileImageRequestVo.getProfileImage());
        chatService.updateProfileImage(updateProfileImageRequestVo.toUpdateProfileImageRequestDto());
    }
}