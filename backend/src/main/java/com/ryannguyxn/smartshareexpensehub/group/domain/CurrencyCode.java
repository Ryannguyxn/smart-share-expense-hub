package com.ryannguyxn.smartshareexpensehub.group.domain;

import java.util.Objects;
import java.util.regex.Pattern;

public record CurrencyCode(String value) {

    private static final Pattern FORMAT = Pattern.compile("^[A-Z]{3}$");

    public CurrencyCode {
        Objects.requireNonNull(value, "currencyCode must not be null");

        if (!FORMAT.matcher(value).matches()) {
            throw new IllegalArgumentException(
                    "currencyCode must be a 3-letter uppercase code"
            );
        }
    }

    @Override
    public String toString() {
        return value;
    }
}