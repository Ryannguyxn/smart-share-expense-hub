package com.ryannguyxn.smartshareexpensehub.user.application.register;

import java.util.Objects;

public record RegisterUserCommand(String email, String rawPassword) {

    public RegisterUserCommand {
        Objects.requireNonNull(email, "email must not be null");
        Objects.requireNonNull(rawPassword, "rawPassword must not be null");

        if (email.isBlank()) {
            throw new IllegalArgumentException("email must not be blank");
        }

        if (rawPassword.isBlank()) {
            throw new IllegalArgumentException("rawPassword must not be blank");
        }
    }
}