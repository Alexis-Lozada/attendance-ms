package mx.edu.uteq.idgs12.chat_ms.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Endpoint para que el frontend se conecte
        registry.addEndpoint("/ws-chat")
                .setAllowedOrigins(
                        "*"
                )
                .withSockJS(); // fallback
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic"); // prefijo para los mensajes de salida
        registry.setApplicationDestinationPrefixes("/app"); // prefijo para los mensajes entrantes
    }
}
