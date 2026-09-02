package com.ryannguyxn.smartshareexpensehub.user.application.authenticate;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthenticateUserCommandTest {

    @Test
    void shouldRejectNullEmail() {
        assertThrows(
                NullPointerException.class,
                () -> new AuthenticateUserCommand(
                        null,
                        "StrongPassword123!"
                )
        );
    }

    @Test
    void shouldRejectNullRawPassword() {
        assertThrows(
                NullPointerException.class,
                () -> new AuthenticateUserCommand(
                        "user@example.com",
                        null
                )
        );
    }

    @Test
    void shouldRejectBlankEmail() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new AuthenticateUserCommand(
                        "   ",
                        "StrongPassword123!"
                )
        );
    }

    @Test
    void shouldRejectBlankRawPassword() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new AuthenticateUserCommand(
                        "user@example.com",
                        "   "
                )
        );
    }

    @Test
    void shouldRedactRawPasswordInToString() {
        String rawPassword = "StrongPassword123!";

        AuthenticateUserCommand command =
                new AuthenticateUserCommand(
                        "user@example.com",
                        rawPassword
                );

        String result = command.toString();

        assertFalse(result.contains(rawPassword));
        assertTrue(result.contains("[REDACTED]"));
    }
}