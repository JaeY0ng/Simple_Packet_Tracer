package com.example.simple_packet_tracer.websocket.config;

import com.example.simple_packet_tracer.websocket.PacketWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final PacketWebSocketHandler handler;

    public WebSocketConfig(PacketWebSocketHandler handler) {
        this.handler = handler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(handler, "/packets").setAllowedOrigins("*");
    }
}
