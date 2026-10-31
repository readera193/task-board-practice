package com.example.taskboard.infrastructure.persistence;

import com.example.taskboard.domain.task.Task;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JpaTaskRepository extends JpaRepository<Task, Long> {

    Page<Task> findAllByUserUsername(String username, Pageable pageable);

    Optional<Task> findByIdAndUserUsername(Long id, String username);

    @Modifying
    @Query(
            "delete from Task t where t.user.id in "
                    + "(select u.id from User u where u.demo = true and u.createdAt < :cutoff)")
    int deleteOfDemoUsersCreatedBefore(@Param("cutoff") LocalDateTime cutoff);
}
