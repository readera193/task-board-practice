package com.example.taskboard.application.user;

import com.example.taskboard.domain.user.User;
import java.util.Optional;

public interface UserRepository {

    Optional<User> findByUsername(String username);

    User save(User user);

    boolean existsByUsername(String username);
}
