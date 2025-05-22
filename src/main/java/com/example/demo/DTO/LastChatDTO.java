package com.example.demo.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LastChatDTO {
    private long chatId;
    private String userFirstName;
    private String userLastName;
    private String logo;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
    private boolean lastMessageIsRead;

}
