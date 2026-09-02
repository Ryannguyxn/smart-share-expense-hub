package com.ryannguyxn.smartshareexpensehub.user.application.authenticate;

import java.util.Objects;

public record AuthenticateUserResult(String accessToken) {

    public AuthenticateUserResult {
        Objects.requireNonNull(
                accessToken,
                "accessToken must not be null"
        );

        if(accessToken.isBlank()) {
            throw new IllegalArgumentException(
                    "accessToken must not be blank"
            );
        }
    }
}
