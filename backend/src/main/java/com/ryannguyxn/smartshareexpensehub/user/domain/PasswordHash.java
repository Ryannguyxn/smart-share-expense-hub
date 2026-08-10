package com.ryannguyxn.smartshareexpensehub.user.domain;

import java.util.Objects;

public record PasswordHash(String value) {

    public PasswordHash {
        Objects.requireNonNull(value, "Password hash must not be null");

        if (value.isBlank()) {
            throw new IllegalArgumentException("Password hash must not be blank");
        }

        if (value.length() > 255) {
            throw new IllegalArgumentException("Password hash must not exceed 255 characters");
        }
    }

    @Override
    public String toString() {
        return "[REDACTED]";
    }
}