package com.ryannguyxn.smartshareexpensehub.user.infrastructure.security;

import com.ryannguyxn.smartshareexpensehub.user.application.port.PasswordVerifier;
import com.ryannguyxn.smartshareexpensehub.user.domain.PasswordHash;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Objects;

public final class BCryptPasswordVerifier implements PasswordVerifier {
    private final PasswordEncoder passwordEncoder;

    public BCryptPasswordVerifier(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = Objects.requireNonNull(
                passwordEncoder,
                "passwordEncoder must not be null"
        );
    }

    @Override
    public boolean matches(String rawPassword, PasswordHash passwordHash) {
        Objects.requireNonNull(rawPassword, "rawPassword must not be null");
        Objects.requireNonNull(passwordHash, "passwordHash must not be null");

        return passwordEncoder.matches(rawPassword, passwordHash.value());
    }
}

