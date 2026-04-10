package com.agileboard.taskboard.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker // Spring Boot'a "WebSocket santralini aç!" emrini verdik.
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // "/topic" ile başlayan kanallara abone olan herkes yayını duyacak (Radyo kanalı).
        config.enableSimpleBroker("/topic");
        // "/app" ile başlayan mesajlar Backend'e (Controller'a) yönlendirilecek.
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Frontend bu adres üzerinden WebSocket bağlantısını kuracak.
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS();
    }
}
