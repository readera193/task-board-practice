package com.example.taskboard.presentation.auth;

import com.example.taskboard.infrastructure.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtService jwtService
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public LoginResponse login(
            @Valid @RequestBody LoginRequest request
    ) {
        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.username(),
                                request.password()
                        )
                );

        String accessToken =
                jwtService.generateToken(authentication);

        return new LoginResponse(
                authentication.getName(),
                accessToken,
                "Bearer"
        );
    }
    
    @GetMapping("/me")
    public CurrentUserResponse me(
            Authentication authentication
    ) {
        return new CurrentUserResponse(
                authentication.getName()
        );
    }

    @GetMapping("/admin-test")
    public String adminTest() {
        return "admin only";
    }
}