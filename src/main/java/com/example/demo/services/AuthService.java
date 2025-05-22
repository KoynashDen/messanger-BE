package com.example.demo.services;

import com.example.demo.DTO.*;
import com.example.demo.entity.PasswordReset;
import com.example.demo.entity.User;
import com.example.demo.entity.UserConfirmation;
import com.example.demo.models.Status;
import com.example.demo.repository.PasswordResetRepository;
import com.example.demo.repository.UserConfirmationRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.utils.Constants;
import com.example.demo.utils.UUIDGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserConfirmationRepository userConfirmationRepository;

    @Autowired
    private PasswordResetRepository passwordResetRepository;

    public RegisterResponseDTO registerUser(CreateUserDTO user) throws Exception {
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser.isPresent() && existingUser.get().isVerified()) {
            throw new Exception("User already exist");
        }
        if(existingUser.isPresent() && !existingUser.get().isVerified()) {
            userRepository.deleteUserById(existingUser.get().getId());
        }
        User newUser = User.builder()
                .name(user.getName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .password(passwordEncoder.encode(user.getPassword()))
                .logo(convertToBase64(user.getLogo()))
                .build();

        userRepository.save(newUser);

        String code = UUIDGenerator
                .generateRandom6Digits();

        UserConfirmation confirmation = userConfirmationRepository.save(new UserConfirmation(code, newUser));

        CompletableFuture.runAsync(() -> {
            try {
                emailService.sendEmail(newUser.getEmail(), "Registration Confirmation", buildRegistrationConfirmationContent(confirmation.getCode()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        return new RegisterResponseDTO(newUser, confirmation.getId());
    }

    public ConfirmationRegisterResponseDTO confirmRegisterUser(String confirmationCode, Long confirmationId){
        Optional<UserConfirmation> confirmationRealCode = userConfirmationRepository.findUserConfirmationById(confirmationId);

        if (confirmationRealCode.isEmpty()) {
            return new ConfirmationRegisterResponseDTO(Status.FAILED, "Confirmation Not Exist");
        }

        if(confirmationRealCode.get().getCode().equals(confirmationCode)) {
            User updatedUser = confirmationRealCode.get().getUser();
            updatedUser.setVerified(true);
            userRepository.save(updatedUser);
            return new ConfirmationRegisterResponseDTO(Status.OK, "User confirmation successfully");
        }else{
            return new ConfirmationRegisterResponseDTO(Status.FAILED, "User is not verified");
        }


    }

    public String loginUser(LoginDTO loginDTO) throws Exception {
        Optional<User> existingUser = userRepository.findByEmail(loginDTO.getEmail());
        if (existingUser.isEmpty()) {
            throw new Exception("User not found");
        }
        if(!passwordEncoder.matches(loginDTO.getPassword(),existingUser.get().getPassword())){
            throw new Exception("Wrong password");
        }

        if(!existingUser.get().isVerified()){
            throw new Exception("Confirm your email");
        }

        return jwtUtil.generateToken(String.valueOf(existingUser.get().getId()),existingUser.get().getEmail(), Constants.TOKEN_EXPIRES_IN);

    }


    public StatusResponseDTO requestResetPassword(String email){
        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isEmpty()) {
            return new StatusResponseDTO(Status.FAILED, "Email not found");
        }
        String resetId = UUIDGenerator.generateUUID();

        passwordResetRepository.save(new PasswordReset(resetId, existingUser.get()));


        CompletableFuture.runAsync(() -> {
            try {
                emailService.sendEmail(existingUser.get()
                        .getEmail(), "Password reset", requestPasswordResetContent(resetId));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        return new StatusResponseDTO(Status.OK, "Password reset successfully");

    }
    
    public StatusResponseDTO resetPassword(ResetPasswordDTO resetPasswordDTO){
        Optional<PasswordReset> passwordReset = passwordResetRepository.findById(resetPasswordDTO.getResetId());
        if (passwordReset.isEmpty()) {
            return new StatusResponseDTO(Status.FAILED, "Password reset not found");
        }
        User updateUser = passwordReset.get().getUser();
        updateUser.setPassword(passwordEncoder.encode(resetPasswordDTO.getNewPassword()));
        userRepository.save(updateUser);
        return new StatusResponseDTO(Status.OK, "Password reset successfully");
    }




    public static String convertToBase64(MultipartFile file) throws Exception {
        byte[] fileContent = file.getBytes();
        return Base64.getEncoder().encodeToString(fileContent);
    }

    public static String buildRegistrationConfirmationContent(String confirmationCode) {
        return "<p>Dear user,</p>" +
                "<p>Thank you for registering. Please use the following code to confirm your registration:</p>" +
                "<p><strong>" + confirmationCode + "</strong></p>" +
                "<p>If you did not request this, please ignore this email.</p>";
    }

    public static String requestPasswordResetContent(String link){
        return "<p>Hello,</p>" +
                "<p>We received a request to reset your password. Please use the following link to reset your password:</p>" +
                "<p><a href='https://localhost:5173/resetpassword?resetid=" + link + "'>Reset Password</a></p>" +
                "<p>If you did not request this, you can safely ignore this email.</p>";
    }

}
