package com.example.taskboard.application.user;

import com.example.taskboard.domain.user.Role;
import com.example.taskboard.domain.user.User;
import java.time.Clock;
import java.time.LocalDateTime;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Clock clock;

    public UserService(
            UserRepository userRepository, PasswordEncoder passwordEncoder, Clock clock) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.clock = clock;
    }

    @Transactional
    public UserResult register(String username, String password) {
        String normalizedUsername = username.trim();

        if (userRepository.existsByUsername(normalizedUsername)) {
            throw UserErrors.usernameAlreadyExists();
        }

        String encodedPassword = passwordEncoder.encode(password);

        User user =
                new User(
                        null,
                        normalizedUsername,
                        encodedPassword,
                        Role.USER,
                        false,
                        LocalDateTime.now(clock));

        try {
            return UserResult.from(userRepository.save(user));
        } catch (DataIntegrityViolationException e) {
            // 併發註冊同一帳號時，先查後寫會漏過 existsByUsername，由 unique 約束兜底並轉成 409
            throw UserErrors.usernameAlreadyExists();
        }
    }
}
