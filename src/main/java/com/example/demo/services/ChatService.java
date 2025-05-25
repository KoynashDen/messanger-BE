package com.example.demo.services;

import com.example.demo.DTO.LastChatDTO;
import com.example.demo.DTO.MessageDTO;
import com.example.demo.DTO.MessageResponseDTO;
import com.example.demo.DTO.StatusResponseDTO;
import com.example.demo.entity.Chat;
import com.example.demo.entity.Message;
import com.example.demo.entity.User;
import com.example.demo.models.Status;
import com.example.demo.repository.ChatRepository;
import com.example.demo.repository.MessageRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ChatService {
    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageRepository messageRepository;

    private String getCurrentUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }

    public Object createNewChat(Long userId) {
        Optional<User> currentUser = userRepository.findByEmail(getCurrentUserEmail());
        if (currentUser.isEmpty()) {
            return new StatusResponseDTO(Status.FAILED, "User not found");
        }
        Optional<User> companionUser = userRepository.findById(userId);
        if (companionUser.isEmpty()) {
            return new StatusResponseDTO(Status.FAILED, "User not found");
        }
        return chatRepository.save(new Chat(currentUser.get(), companionUser.get()));
    }

    public Object getMyLastChats(String search) {
        Optional<User> currentUser = userRepository.findByEmail(getCurrentUserEmail());
        if (currentUser.isEmpty()) {
            return new StatusResponseDTO(Status.FAILED, "User not found");
        }
        List<Chat> myChats = chatRepository.findByUser1OrUser2(currentUser.get(), currentUser.get());

        String searchLower = search == null ? "" : search.toLowerCase();

        return myChats.stream()
                .filter(chat -> {
                    User otherUser = !chat.getUser1().equals(currentUser.get()) ? chat.getUser1() : chat.getUser2();
                    return otherUser.getName().toLowerCase().contains(searchLower) ||
                            otherUser.getLastName().toLowerCase().contains(searchLower);
                })
                .map(chat -> {
                    User otherUser = !chat.getUser1().equals(currentUser.get()) ? chat.getUser1() : chat.getUser2();
                    return LastChatDTO.builder()
                            .logo(otherUser.getLogo())
                            .userFirstName(otherUser.getName())
                            .userLastName(otherUser.getLastName())
                            .chatId(chat.getId())
                            .build();
                })
                .collect(Collectors.toList());
    }


    public MessageResponseDTO processMessage(MessageDTO messageDTO) {
        Optional<Chat> chat = chatRepository.findById(messageDTO.getChatId());
        Optional<User> sender = userRepository.findById(messageDTO.getSenderId());
        Message newMessge = Message.builder()
                .chat(chat.get())
                .content(messageDTO.getMessage())
                .read(false)
                .user(sender.get())
                .build();
        messageRepository.save(newMessge);
        return MessageResponseDTO.builder()
                .id(newMessge.getId())
                .message(newMessge.getContent())
                .timestamp(newMessge.getTimestamp())
                .senderId(sender.get().getId())
                .receivers(List.of(chat.get().getUser1().getId(),chat.get().getUser2().getId()))
                .build();
    }
}
