package com.example.demo.controllers;

import com.example.demo.DTO.*;
import com.example.demo.entity.User;
import com.example.demo.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public RegisterResponseDTO register(@ModelAttribute CreateUserDTO user) throws Exception {
        return authService.registerUser(user);
    }

    @PostMapping("/confirmation")
    public ConfirmationRegisterResponseDTO registerConfirmation(@RequestParam String confirmationCode, @RequestParam Long confirmationId){
        return authService.confirmRegisterUser(confirmationCode, confirmationId);
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginDTO loginDTO) throws Exception {
        return authService.loginUser(loginDTO);
    }

    @PostMapping("/password/request")
    public StatusResponseDTO requestPasswordReset(@RequestParam String email) {
        return authService.requestResetPassword(email);
    }

    @PostMapping("/password/confirm")
    public StatusResponseDTO confirmPasswordReset(@RequestBody ResetPasswordDTO resetPasswordDTO) {
        return authService.resetPassword(resetPasswordDTO);
    }
}
