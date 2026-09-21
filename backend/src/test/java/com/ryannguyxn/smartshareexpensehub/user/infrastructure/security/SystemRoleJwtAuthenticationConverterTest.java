package com.ryannguyxn.smartshareexpensehub.user.infrastructure.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class SystemRoleJwtAuthenticationConverterTest {

    private final SystemRoleJwtAuthenticationConverter converter =
            new SystemRoleJwtAuthenticationConverter();

    @Test
    void shouldConvertUserSystemRoleToGrantedAuthority() {
        String userId = "123e4567-e89b-12d3-a456-426614174000";

        Jwt jwt = Jwt.withTokenValue("test-token")
                .header("alg", "HS256")
                .subject(userId)
                .issuedAt(Instant.parse("2026-01-01T00:00:00Z"))
                .expiresAt(Instant.parse("2026-01-01T01:00:00Z"))
                .claim("systemRole", "USER")
                .build();

        AbstractAuthenticationToken authentication =
                converter.convert(jwt);

        assertEquals(userId, authentication.getName());

        assertTrue(
                authentication.getAuthorities().stream()
                        .anyMatch(authority ->
                                authority.getAuthority().equals("ROLE_USER")
                        )
        );
    }

    @Test
    void shouldConvertAdminSystemRoleToGrantedAuthority() {
        Jwt jwt = Jwt.withTokenValue("test-token")
                .header("alg", "HS256")
                .subject("123e4567-e89b-12d3-a456-426614174000")
                .claim("systemRole", "ADMIN")
                .build();

        AbstractAuthenticationToken authentication = converter.convert(jwt);

        assertTrue(
                authentication.getAuthorities().stream()
                        .anyMatch(authority ->
                                authority.getAuthority().equals("ROLE_ADMIN")
                        )
        );
    }

    @Test
    void shouldRejectUnknownSystemRole() {
        Jwt jwt = Jwt.withTokenValue("test-token")
                .header("alg", "HS256")
                .subject("123e4567-e89b-12d3-a456-426614174000")
                .claim("systemRole", "MEMBER")
                .build();

        assertThrows(
                OAuth2AuthenticationException.class,
                () -> converter.convert(jwt)
        );
    }

    @Test
    void shouldRejectMissingSystemRole() {
        Jwt jwt = Jwt.withTokenValue("test-token")
                .header("alg", "HS256")
                .subject("123e4567-e89b-12d3-a456-426614174000")
                .build();

        assertThrows(
                OAuth2AuthenticationException.class,
                () -> converter.convert(jwt)
        );
    }
}