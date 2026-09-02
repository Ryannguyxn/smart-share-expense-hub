package com.ryannguyxn.smartshareexpensehub.user.application.authenticate;

import java.util.Objects;

public record AuthenticateUserCommand(String email, String rawPassword) {

    public AuthenticateUserCommand {
        Objects.requireNonNull(email, "email must not be null");
        Objects.requireNonNull(rawPassword, "rawPassword must not be null");

        if (email.isBlank()) {
            throw new IllegalArgumentException("email must not be blank");
        }

        if (rawPassword.isBlank()) {
            throw new IllegalArgumentException("rawPassword must not be blank");
        }
    }

    @Override
    public String toString() {
        return "AuthenticateUserCommand[email=" + email + ", rawPassword=[REDACTED]]";
    }
}
