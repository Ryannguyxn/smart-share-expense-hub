package com.ryannguyxn.smartshareexpensehub.user.application.register;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RegisterUserCommandTest {

    @Test
    void shouldRedactRawPasswordInToString() {
        String rawPassword = "password-that-must-not-be-logged";
        RegisterUserCommand command =
                new RegisterUserCommand("member@example.test", rawPassword);

        String renderedCommand = command.toString();

        assertAll(
                () -> assertFalse(renderedCommand.contains(rawPassword)),
                () -> assertTrue(renderedCommand.contains("rawPassword=[REDACTED]")),
                () -> assertTrue(renderedCommand.contains("email=member@example.test"))
        );
    }
}
