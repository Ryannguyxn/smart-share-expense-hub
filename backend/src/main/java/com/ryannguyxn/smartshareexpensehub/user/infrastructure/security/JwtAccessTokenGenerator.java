package com.ryannguyxn.smartshareexpensehub.user.infrastructure.security;

import com.ryannguyxn.smartshareexpensehub.user.application.port.AccessTokenGenerator;
import com.ryannguyxn.smartshareexpensehub.user.domain.UserSystemRole;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import java.time.Instant;
import java.time.Clock;
import java.time.Duration;
import java.util.Objects;
import java.util.UUID;

public final class JwtAccessTokenGenerator implements AccessTokenGenerator {

    private final JwtEncoder jwtEncoder;
    private final Clock clock;
    private final Duration accessTokenTtl;

    public JwtAccessTokenGenerator(
            JwtEncoder jwtEncoder,
            Clock clock,
            Duration accessTokenTtl
    ) {
        this.jwtEncoder = Objects.requireNonNull(
                jwtEncoder,
                "jwtEncoder must not be null"
        );

        this.clock = Objects.requireNonNull(
                clock,
                "clock must not be null"
        );

        this.accessTokenTtl = Objects.requireNonNull(
                accessTokenTtl,
                "accessTokenTtl must not be null"
        );

        if (accessTokenTtl.isZero() || accessTokenTtl.isNegative()) {
            throw new IllegalArgumentException(
                    "accessTokenTtl must be positive"
            );
        }
    }

    @Override
    public String generate(UUID userId, UserSystemRole systemRole) {
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(systemRole, "systemRole must not be null");

        Instant issuedAt = clock.instant();
        Instant expiresAt = issuedAt.plus(accessTokenTtl);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(userId.toString())
                .issuedAt(issuedAt)
                .expiresAt(expiresAt)
                .claim("systemRole", systemRole.name())
                .build();

        return jwtEncoder
                .encode(JwtEncoderParameters.from(claims))
                .getTokenValue();
    }
}
