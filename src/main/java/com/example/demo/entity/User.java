package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "users")
public class User {


    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private String lastName;

    @Lob
    private String logo;

    private String email;

    @JsonIgnore
    private String password;

    private boolean verified = false;
}
