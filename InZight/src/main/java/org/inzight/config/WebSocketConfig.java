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
        // Native WebSocket endpoint cho mobile/web client
        registry.addEndpoint("/ws")
                .addInterceptors(new JwtHandshakeInterceptor(jwtUtil))
                .setAllowedOriginPatterns("*");

        // SockJS fallback (nếu cần dùng từ web cũ)
        registry.addEndpoint("/ws-sockjs")
                .addInterceptors(new JwtHandshakeInterceptor(jwtUtil))
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
