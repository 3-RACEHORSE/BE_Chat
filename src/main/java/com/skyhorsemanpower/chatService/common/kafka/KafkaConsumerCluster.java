package com.skyhorsemanpower.chatService.common.kafka;
import com.skyhorsemanpower.chatService.chat.application.ChatService;
import com.skyhorsemanpower.chatService.chat.data.vo.BeforeChatRoomVo;
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
//    @KafkaListener(topics = "auction-close-topic", groupId = "${spring.kafka.consumer.group-id}")
//    public void consumeAuction(@Payload LinkedHashMap<String, Object> message,
//        @Headers MessageHeaders messageHeaders) {
//        log.info("consumer: success >>> message: {}, headers: {}", message.toString(),
//            messageHeaders);
//        //message를 PaymentReadyVo로 변환
//        BeforeChatRoomVo beforeChatRoomVo = BeforeChatRoomVo.builder()
//            .auctionUuid(message.get("auctionUuid").toString())
//            .memberUuids((List<String>) message.get("memberUuids"))
//            .build();
//        log.info("auctionUuid : {}", beforeChatRoomVo.getAuctionUuid());
//        log.info("memberUuids : {}", beforeChatRoomVo.getMemberUuids());
//        chatService.convertToChatRoomData(beforeChatRoomVo.toBeforeChatRoomDto());
//    }
    @KafkaListener(topics = "payment-close-topic", groupId = "${spring.kafka.consumer.group-id}")
    public void consumePayment(@Payload LinkedHashMap<String, Object> message,
        @Headers MessageHeaders messageHeaders) {
        log.info("consumer: success >>> message: {}, headers: {}", message.toString(),
            messageHeaders);
        //message를 PaymentReadyVo로 변환
        BeforeChatRoomVo beforeChatRoomVo = BeforeChatRoomVo.builder()
            .auctionUuid(message.get("auctionUuid").toString())
            .memberUuids((List<String>) message.get("memberUuids"))
            .build();
        log.info("auctionUuid : {}", beforeChatRoomVo.getAuctionUuid());
        log.info("memberUuids : {}", beforeChatRoomVo.getMemberUuids());
        chatService.convertToChatRoomData(beforeChatRoomVo.toBeforeChatRoomDto());
    }
}