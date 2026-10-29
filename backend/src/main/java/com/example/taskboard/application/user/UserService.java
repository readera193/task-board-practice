package com.example.taskboard.application.user;

import com.example.taskboard.domain.user.User;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResult register(
            String username,
            String password
    ) {
        String normalizedUsername = username.trim();

        if (userRepository.existsByUsername(normalizedUsername)) {
            throw UserErrors.usernameAlreadyExists();
        }

        String encodedPassword =
                passwordEncoder.encode(password);

        User user = new User(
                null,
                normalizedUsername,
                encodedPassword,
                "USER"
        );

        try {
            return UserResult.from(userRepository.save(user));
        } catch (DataIntegrityViolationException e) {
            // 併發註冊同一帳號時，先查後寫會漏過 existsByUsername，由 unique 約束兜底並轉成 409
            throw UserErrors.usernameAlreadyExists();
        }
    }
}
