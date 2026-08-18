package com.ryannguyxn.smartshareexpensehub.user.infrastructure.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BCryptPasswordHashGeneratorTest {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final BCryptPasswordHashGenerator passwordHashGenerator =
            new BCryptPasswordHashGenerator(passwordEncoder);

    @Test
    void shouldGenerateBcryptHashThatMatchesRawPassword() {
        String rawPassword = "correct-horse-battery-staple";

        String hash = passwordHashGenerator.hash(rawPassword).value();

        assertTrue(passwordEncoder.matches(rawPassword, hash));
        assertNotEquals(rawPassword, hash);
    }

    @Test
    void shouldRejectNullRawPassword() {
        assertThrows(
                NullPointerException.class,
                () -> passwordHashGenerator.hash(null)
        );
    }
}
