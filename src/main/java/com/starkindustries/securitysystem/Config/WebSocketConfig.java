package com.starkindustries.securitysystem.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Configuración general del servidor WebSocket.
 *
 * Se habilita STOMP como protocolo de mensajería sobre WebSocket.
 * Los clientes se conectarán a "/ws/alerts" y recibirán notificaciones desde "/topic/alerts".
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws/alerts")
                .setAllowedOriginPatterns("*")
                .withSockJS(); // Permite fallback en navegadores sin soporte WebSocket
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic"); // prefijo de salida (servidor → cliente)
        config.setApplicationDestinationPrefixes("/app"); // prefijo de entrada (cliente → servidor)
    }
}
