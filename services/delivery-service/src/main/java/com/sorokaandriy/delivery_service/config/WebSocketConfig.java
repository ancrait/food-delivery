package com.sorokaandriy.delivery_service.config;

import com.sorokaandriy.delivery_service.websocket.DeliveryWebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private final DeliveryWebSocketHandler deliveryWebSocketHandler;
    private final JwtHandshakeInterceptor jwtHandshakeInterceptor;

    // handle any request like /ws/delivery and redirects them to deliveryWebSocketHandler
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(deliveryWebSocketHandler, "/ws/delivery")
                .addInterceptors(jwtHandshakeInterceptor)
                .setAllowedOrigins("*");
    }
}
