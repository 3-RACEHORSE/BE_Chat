package com.skyhorsemanpower.chatService.common.kafka;

import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaProducerCluster {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendMessage(String topicName, Object object) {
        CompletableFuture<SendResult<String, Object>> future =
            kafkaTemplate.send(topicName, object);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("producer: success >>> message: {}, offset: {}",
                    result.getProducerRecord().value().toString(),
                    result.getRecordMetadata().offset());
            } else {
                log.info("producer: failure >>> message: {}", ex.getMessage());
            }
        });
    }
}