package com.example.demo.controllers;

import com.example.demo.entity.Chat;
import com.example.demo.services.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chat")
public class ChatController {
    @Autowired
    private ChatService chatService;

    @PutMapping("/create")
    public Object createChat(@RequestParam Long userId) {
        return chatService.createNewChat(userId);
    }

    @GetMapping("/chats")
    public Object getMyChats(@RequestParam String search){
        return chatService.getMyLastChats(search);
    }



}
