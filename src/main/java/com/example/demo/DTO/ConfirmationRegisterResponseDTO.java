package com.example.demo.DTO;

import com.example.demo.models.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConfirmationRegisterResponseDTO {
    private Status status;
    private String message;


}
