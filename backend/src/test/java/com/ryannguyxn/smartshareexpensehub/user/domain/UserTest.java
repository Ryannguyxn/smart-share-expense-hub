package com.ryannguyxn.smartshareexpensehub.user.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserTest {

    @Test
    void shouldNormalizeEmailToLowercase() {
        Email email = new Email("  Ryan.Nguyen@Example.COM  ");

        assertEquals("ryan.nguyen@example.com", email.value());
    }

    @Test
    void shouldRejectInvalidEmail() {
        assertThrows(IllegalArgumentException.class, () -> new Email("invalid-email"));
    }

    @Test
    void shouldNotExposePasswordHashInToString() {
        PasswordHash passwordHash = new PasswordHash("hashed-password-value");

        assertEquals("[REDACTED]", passwordHash.toString());
    }

    @Test
    void shouldRegisterStandardUser() {
        UUID id = UUID.randomUUID();
        Email email = new Email("user@example.com");
        PasswordHash passwordHash = new PasswordHash("hashed-password-value");
        Instant now = Instant.parse("2026-08-10T08:00:00Z");

        User user = User.register(id, email, passwordHash, now);

        assertEquals(id, user.id());
        assertEquals(email, user.email());
        assertEquals(UserSystemRole.USER, user.systemRole());
        assertEquals(now, user.createdAt());
        assertEquals(now, user.updatedAt());
    }

    @Test
    void shouldRejectUpdatedTimeBeforeCreatedTime() {
        Instant createdAt = Instant.parse("2026-08-10T08:00:00Z");
        Instant updatedAt = createdAt.minusSeconds(1);

        assertThrows(
                IllegalArgumentException.class,
                () -> User.reconstitute(
                        UUID.randomUUID(),
                        new Email("user@example.com"),
                        new PasswordHash("hashed-password-value"),
                        UserSystemRole.USER,
                        createdAt,
                        updatedAt
                )
        );
    }
}