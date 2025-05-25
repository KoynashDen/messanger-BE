package com.example.demo.repository;

import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);


    void deleteUserById(Long id);


    @Query("SELECT u FROM users u WHERE u.id <> :currentUserId AND " +
            "u.id NOT IN (" +
            "  SELECT CASE WHEN c.user1.id = :currentUserId THEN c.user2.id ELSE c.user1.id END " +
            "  FROM Chat c " +
            "  WHERE c.user1.id = :currentUserId OR c.user2.id = :currentUserId" +
            ")")
    List<User> findUsersWithoutChatWith(@Param("currentUserId") Long currentUserId);

}
