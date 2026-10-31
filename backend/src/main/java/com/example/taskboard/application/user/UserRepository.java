package com.example.taskboard.application.user;

import com.example.taskboard.domain.user.User;
import java.time.LocalDateTime;
import java.util.Optional;

public interface UserRepository {

    Optional<User> findByUsername(String username);

    User save(User user);

    boolean existsByUsername(String username);

    /** 刪除建立時間早於 cutoff 的 Demo 帳號，回傳刪除筆數（其任務須先刪除）。 */
    int deleteDemoUsersCreatedBefore(LocalDateTime cutoff);
}
