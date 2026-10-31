package com.example.taskboard.presentation.auth;

import com.example.taskboard.application.user.DemoLoginService;
import com.example.taskboard.infrastructure.security.JwtService;
import com.example.taskboard.shared.exception.NotFoundException;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "認證", description = "登入取得 JWT 與查詢目前使用者")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final DemoLoginService demoLoginService;
    private final boolean demoLoginEnabled;

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            DemoLoginService demoLoginService,
            @Value("${app.demo-login.enabled:false}") boolean demoLoginEnabled) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.demoLoginService = demoLoginService;
        this.demoLoginEnabled = demoLoginEnabled;
    }

    @SecurityRequirements
    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.username(), request.password()));

        String accessToken = jwtService.generateToken(authentication);

        return new LoginResponse(authentication.getName(), accessToken, "Bearer");
    }

    /** Demo 體驗登入：免註冊，一鍵取得隨機帳號與範例任務。僅展示環境啟用，未啟用時視為不存在。 */
    @SecurityRequirements
    @PostMapping("/demo")
    public LoginResponse demoLogin() {
        if (!demoLoginEnabled) {
            throw new NotFoundException("DEMO_LOGIN_DISABLED", "Demo 體驗登入未啟用");
        }
        String username = demoLoginService.createDemoUser();
        Authentication authentication =
                UsernamePasswordAuthenticationToken.authenticated(
                        username, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
        return new LoginResponse(username, jwtService.generateToken(authentication), "Bearer");
    }

    @GetMapping("/me")
    public CurrentUserResponse me(Authentication authentication) {
        return new CurrentUserResponse(authentication.getName());
    }
}
