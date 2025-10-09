package org.inzight.config;

import lombok.RequiredArgsConstructor;
import org.inzight.security.JwtHandshakeInterceptor;
import org.inzight.security.JwtUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final JwtUtil jwtUtil;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Đây là endpoint FE sẽ connect tới: /ws
        registry.addEndpoint("/ws")
                .addInterceptors(new JwtHandshakeInterceptor(jwtUtil)) // ✅ Thêm interceptor kiểm JWT
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Prefix cho các topic mà client sẽ subscribe
        registry.enableSimpleBroker("/topic", "/queue");
        // Prefix cho các API gửi từ FE lên BE
        registry.setApplicationDestinationPrefixes("/app");
    }
}
