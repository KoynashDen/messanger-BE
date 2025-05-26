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

import java.time.LocalDateTime;
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

    public Object getMyLastChats(){
        Optional<User> currentUser = userRepository.findByEmail(getCurrentUserEmail());
        if (currentUser.isEmpty()) {
            return new StatusResponseDTO(Status.FAILED, "User not found");
        }
        List<Chat> myChats = chatRepository.findByUser1OrUser2(currentUser.get(), currentUser.get());

        return myChats.stream().map(Chat->{
            String logo = !Chat.getUser1().equals(currentUser.get())?Chat.getUser1().getLogo():Chat.getUser2().getLogo();
            String firstName = !Chat.getUser1().equals(currentUser.get())?Chat.getUser1().getName():Chat.getUser2().getName();
            String lastName = !Chat.getUser1().equals(currentUser.get())?Chat.getUser1().getLastName():Chat.getUser2().getLastName();

            return LastChatDTO.builder()
                    .logo(logo)
                    .userFirstName(firstName)
                    .userLastName(lastName)
                    .chatId(Chat.getId())
                    .build();
        }).collect(Collectors.toList());

    }


    public MessageResponseDTO processMessage(MessageDTO messageDTO) {
        Optional<Chat> chat = chatRepository.findById(messageDTO.getChatId());
        Optional<User> sender = userRepository.findById(messageDTO.getSenderId());
        Message newMessge = new Message(
                chat.get(),
                sender.get(),
                messageDTO.getMessage(),
                LocalDateTime.now(),
                false
        );

        messageRepository.save(newMessge);
        return MessageResponseDTO.builder()
                .id(newMessge.getId())
                .message(newMessge.getContent())
                .timestamp(newMessge.getTimestamp())
                .senderId(sender.get().getId())
                .receivers(List.of(chat.get().getUser1().getId(),chat.get().getUser2().getId()))
                .build();
    }


    public List<Message> getAllMessages(Long chatId) {
        return messageRepository.findAllByChatId(chatId).stream().peek(message -> message.setChat(null)).collect(Collectors.toList());
    }
}
