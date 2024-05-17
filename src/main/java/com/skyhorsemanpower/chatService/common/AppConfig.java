package com.skyhorsemanpower.chatService.common;

import com.skyhorsemanpower.chatService.chat.data.vo.ChatVo;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Sinks;

@Configuration
public class AppConfig {
    @Bean
    public ConcurrentHashMap<String, Sinks.Many<ChatVo>> latestMessages() {
        return new ConcurrentHashMap<>();
    }
}
