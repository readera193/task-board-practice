package com.example.taskboard.infrastructure.security;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

        private final JwtEncoder jwtEncoder;
        private final long expirationMinutes;

        public JwtService(
                        JwtEncoder jwtEncoder,
                        JwtProperties jwtProperties) {
                this.jwtEncoder = jwtEncoder;
                this.expirationMinutes = jwtProperties.expirationMinutes();
        }

        public String generateToken(
                        Authentication authentication) {
                Instant now = Instant.now();
                String authorities = authentication
                                .getAuthorities()
                                .stream()
                                .map(authority -> authority.getAuthority())
                                .collect(Collectors.joining(" "));

                JwtClaimsSet claims = JwtClaimsSet.builder()
                                .subject(authentication.getName())
                                .issuedAt(now)
                                .expiresAt(
                                                now.plus(
                                                                expirationMinutes,
                                                                ChronoUnit.MINUTES))
                                .claim("scope", authorities)
                                .build();

                return jwtEncoder
                                .encode(
                                                JwtEncoderParameters.from(claims))
                                .getTokenValue();
        }
}