package com.example.demo.repository;
import java.util.List;
import com.example.demo.entity.Chat;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    List<Chat> findByUser1OrUser2(User user1, User user2);
}
