package com.example.demo.services;

import com.example.demo.DTO.MessageDTO;
import com.example.demo.DTO.MessageResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.CloseStatus;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class MyWebSocketHandler extends TextWebSocketHandler {

    private final JwtUtil jwtUtil;
    private ChatService chatService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final Map<WebSocketSession, Long> userSessions = new ConcurrentHashMap<>();

    public MyWebSocketHandler(JwtUtil jwtUtil,ChatService chatService) {
        this.jwtUtil = jwtUtil;
        this.chatService = chatService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {
        String query = session.getUri().getQuery();
        String token = extractToken(query);

        if (token == null || !jwtUtil.validateToken(token)) {
            session.close(CloseStatus.NOT_ACCEPTABLE);
            System.out.println("Invalid token, closing session: " + session.getId());
            return;
        }

        String userId = jwtUtil.extractIdentifier(token);
        userSessions.put(session, Long.valueOf(userId));

        System.out.println("User " + userId + " connected with session: " + session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        MessageDTO newMessageDTO = objectMapper.readValue(message.getPayload(), MessageDTO.class);
        newMessageDTO.setSenderId(userSessions.get(session));
        MessageResponseDTO messageResponseDTO = chatService.processMessage(newMessageDTO);
        List<WebSocketSession> matchingSession = userSessions.entrySet().stream()
                .filter(entry -> messageResponseDTO.getReceivers().contains(entry.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        matchingSession.forEach(k -> {
            try {
                k.sendMessage(new TextMessage(objectMapper.writeValueAsString(messageResponseDTO)));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        userSessions.remove(session);
    }









    private String extractToken(String query) {
        if (query == null) return null;
        for (String param : query.split("&")) {
            String[] parts = param.split("=");
            if (parts.length == 2 && "token".equals(parts[0])) {
                return parts[1];
            }
        }
        return null;
    }
}
