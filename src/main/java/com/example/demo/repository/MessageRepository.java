package com.example.demo.repository;
import java.util.List;
import java.util.Optional;

import com.example.demo.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message>  findAllByChatId(Long id);
    Optional<Message> findFirstByChatIdOrderByTimestampDesc(Long chatId);
}
