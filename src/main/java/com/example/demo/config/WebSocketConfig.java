package com.example.demo.config;


import com.example.demo.services.ChatService;
import com.example.demo.services.JwtUtil;
import com.example.demo.services.MyWebSocketHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {


    private final JwtUtil jwtUtil;
    private final ChatService chatService;


    public WebSocketConfig(JwtUtil jwtUtil, ChatService chatService) {
        this.jwtUtil = jwtUtil;
        this.chatService = chatService;
    }
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new MyWebSocketHandler(jwtUtil,chatService), "/ws").setAllowedOrigins("*");
    }

    @Bean
    public WebSocketHandler myWebSocketHandler() {
        return new MyWebSocketHandler(jwtUtil,chatService);
    }
}
