package com.ryannguyxn.smartshareexpensehub.user.application.register;

import java.util.Objects;
import java.util.UUID;

public record RegisterUserResult(UUID userId) {

    public RegisterUserResult {
        Objects.requireNonNull(userId, "userId must not be null");
    }
}