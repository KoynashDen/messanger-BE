package com.example.demo.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageResponseDTO {
    private long id;
    private String message;
    private long senderId;
    private LocalDateTime timestamp;
    private List<Long> receivers;
}
