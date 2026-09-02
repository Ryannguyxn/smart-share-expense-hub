package com.ryannguyxn.smartshareexpensehub.user.infrastructure.security;

import com.ryannguyxn.smartshareexpensehub.user.domain.PasswordHash;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

class BCryptPasswordVerifierTest {
    private final PasswordEncoder passwordEncoder =
            new BCryptPasswordEncoder();

    private final BCryptPasswordVerifier passwordVerifier =
            new BCryptPasswordVerifier(passwordEncoder);

    @Test
    void shouldReturnTrueWhenPasswordMatches() {
        String rawPassword = "StrongPassword123!";

        String encoded = passwordEncoder.encode(rawPassword);

        PasswordHash passwordHash = new PasswordHash(encoded);

        assertTrue(passwordVerifier.matches(rawPassword, passwordHash));
    }

    @Test
    void shouldReturnFalseWhenPasswordDoesNotMatch() {
        PasswordHash passwordHash =
                new PasswordHash(
                        passwordEncoder.encode("CorrectPassword123!")
                );

        assertFalse(
                passwordVerifier.matches(
                        "WrongPassword123",
                        passwordHash
                )
        );
    }

    @Test
    void shouldRejectNullRawPassword() {
        PasswordHash passwordHash =
                new PasswordHash(
                        passwordEncoder.encode("CorrectPassword123!")
                );

        assertThrows(
                NullPointerException.class,
                () -> passwordVerifier.matches(null, passwordHash)
        );
    }

    @Test
    void shouldRejectNullPasswordHash() {

        assertThrows(
                NullPointerException.class,
                () -> passwordVerifier.matches("StrongPassword123!", null)
        );
    }
}
