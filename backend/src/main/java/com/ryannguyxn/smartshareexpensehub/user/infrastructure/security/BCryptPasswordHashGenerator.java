package com.ryannguyxn.smartshareexpensehub.user.infrastructure.security;

import com.ryannguyxn.smartshareexpensehub.user.application.port.PasswordHashGenerator;
import com.ryannguyxn.smartshareexpensehub.user.domain.PasswordHash;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Objects;

public final class BCryptPasswordHashGenerator implements PasswordHashGenerator {

    private final PasswordEncoder passwordEncoder;

    public BCryptPasswordHashGenerator(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = Objects.requireNonNull(
                passwordEncoder,
                "passwordEncoder must not be null"
        );
    }

    @Override
    public PasswordHash hash(String rawPassword) {
        Objects.requireNonNull(rawPassword, "rawPassword must not be null");

        return new PasswordHash(passwordEncoder.encode(rawPassword));
    }
}
