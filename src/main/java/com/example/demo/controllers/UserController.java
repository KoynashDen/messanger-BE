package com.example.demo.controllers;

import com.example.demo.entity.User;
import com.example.demo.services.AuthService;
import com.example.demo.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping
    public User getCurrentUser() {
        return userService.getCurrentUser();
    }

    @GetMapping("/without-chat")
    public List<User> getUsersWithoutChat() {
        return userService.getUsersWithoutChat();
    }
}
