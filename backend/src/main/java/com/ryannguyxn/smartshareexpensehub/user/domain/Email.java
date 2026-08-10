package com.ryannguyxn.smartshareexpensehub.user.domain;

import java.util.Locale;
import java.util.Objects;

public record Email(String value) {

    public Email {
        Objects.requireNonNull(value, "Email must not be null");

        value = value.trim().toLowerCase(Locale.ROOT);

        if (value.isEmpty()) {
            throw new IllegalArgumentException("Email must not be blank");
        }

        if (!value.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new IllegalArgumentException("Email format is invalid");
        }
    }
}