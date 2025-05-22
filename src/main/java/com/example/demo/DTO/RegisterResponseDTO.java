package com.example.demo.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.example.demo.entity.User;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterResponseDTO {


    private User user;
    private Long confirmationId;

}
