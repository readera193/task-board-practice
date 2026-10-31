package com.example.taskboard.infrastructure.persistence;

import com.example.taskboard.domain.user.User;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JpaUserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    @Modifying
    @Query("delete from User u where u.demo = true and u.createdAt < :cutoff")
    int deleteDemoUsersCreatedBefore(@Param("cutoff") LocalDateTime cutoff);
}
