package com.example.demo.services;

import com.example.demo.DTO.CreateUserDTO;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    private String getCurrentUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }

    public User getCurrentUser() {
        return userRepository.findByEmail(getCurrentUserEmail()).get();
    }

    public List<User> getUsersWithoutChat(String search) {
        Optional<User> userOpt = userRepository.findByEmail(getCurrentUserEmail());
        if (userOpt.isEmpty()) {
            throw new RuntimeException("Current user not found");
        }
        Long currentUserId = userOpt.get().getId();

        List<User> usersWithoutChat = userRepository.findUsersWithoutChatWith(currentUserId);

        if (search == null || search.isBlank()) {
            return usersWithoutChat;
        }

        String searchLower = search.toLowerCase();

        return usersWithoutChat.stream()
                .filter(u -> u.getName().toLowerCase().contains(searchLower)
                        || u.getLastName().toLowerCase().contains(searchLower))
                .collect(Collectors.toList());
    }


    public User updateProfile(CreateUserDTO updateUserDTO) throws Exception {
        User user = getCurrentUser();


        if (updateUserDTO.getName() != null) {
            user.setName(updateUserDTO.getName());
        }
        if (updateUserDTO.getLastName() != null) {
            user.setLastName(updateUserDTO.getLastName());
        }

        if (updateUserDTO.getLogo() != null && !updateUserDTO.getLogo().isEmpty()) {
            user.setLogo(AuthService.convertToBase64(updateUserDTO.getLogo()));
        }

        return userRepository.save(user);
    }

}
