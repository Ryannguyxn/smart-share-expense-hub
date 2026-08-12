package com.ryannguyxn.smartshareexpensehub.user.domain;

import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

public record Email(String value) {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-z0-9]+(?:[._%+-][a-z0-9]+)*@"
                    + "[a-z0-9]+(?:-[a-z0-9]+)*(?:\\.[a-z0-9]+(?:-[a-z0-9]+)*)+$"
    );

    public Email {
        Objects.requireNonNull(value, "Email must not be null");

        value = value.trim().toLowerCase(Locale.ROOT);

        if (value.isEmpty()) {
            throw new IllegalArgumentException("Email must not be blank");
        }

        if(value.length() > 254) {
            throw new IllegalArgumentException("Email must not exceed 254 characters");
        }

        if(!EMAIL_PATTERN.matcher(value).matches()){
            throw new IllegalArgumentException("Email format is invalid");
        }
    }
}