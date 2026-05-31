package com.example.bakery_shop.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Cấu hình WebSocket với STOMP.
 * Dùng để thông báo real-time (cập nhật trạng thái đơn hàng, thông báo admin).
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Đăng ký endpoint STOMP kết nối WebSocket.
     * Client kết nối qua: ws://localhost:8080/ws
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS(); // Fallback cho trình duyệt không hỗ trợ WebSocket
    }

    /**
     * Cấu hình message broker.
     * - /topic: broadcast tới nhiều subscriber (vd: thông báo đơn hàng mới)
     * - /queue: gửi tới từng user cụ thể
     * - /app: prefix cho @MessageMapping trong Controller
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Prefix destination cho message gửi lên server
        registry.setApplicationDestinationPrefixes("/app");
        // Enable simple in-memory broker cho /topic và /queue
        registry.enableSimpleBroker("/topic", "/queue");
        // Prefix cho user-specific destinations
        registry.setUserDestinationPrefix("/user");
    }
}
