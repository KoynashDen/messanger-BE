package com.example.demo.repository;


import com.example.demo.entity.UserConfirmation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserConfirmationRepository extends JpaRepository<UserConfirmation, Long> {
    Optional<UserConfirmation> findUserConfirmationById(Long userConfirmationId);
}
