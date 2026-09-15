package com.ryannguyxn.smartshareexpensehub.user.infrastructure.security;

import com.ryannguyxn.smartshareexpensehub.user.domain.UserSystemRole;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtAccessTokenGeneratorTest {

    private static final UUID USER_ID =
            UUID.fromString("11111111-1111-1111-1111-111111111111");

    private static final Instant NOW =
            Instant.parse("2026-09-09T12:00:00Z");

    private static final Duration TOKEN_TTL =
            Duration.ofHours(1);

    private final TestJwtEncoder jwtEncoder =
            new TestJwtEncoder();

    private final JwtAccessTokenGenerator tokenGenerator =
            new JwtAccessTokenGenerator(
                    jwtEncoder,
                    Clock.fixed(NOW, ZoneOffset.UTC),
                    TOKEN_TTL
            );

    @Test
    void shouldGenerateAccessTokenWithExpectedClaims() {
        String token = tokenGenerator.generate(
                USER_ID,
                UserSystemRole.USER
        );

        assertEquals("test-access-token", token);

        assertEquals(
                USER_ID.toString(),
                jwtEncoder.parameters.getClaims().getSubject()
        );

        assertEquals(
                UserSystemRole.USER.name(),
                jwtEncoder.parameters
                        .getClaims()
                        .getClaim("systemRole")
        );

        assertEquals(
                NOW,
                jwtEncoder.parameters.getClaims().getIssuedAt()
        );

        assertEquals(
                NOW.plus(TOKEN_TTL),
                jwtEncoder.parameters.getClaims().getExpiresAt()
        );
    }

    @Test
    void shouldRejectNullUserId() {
        assertThrows(
                NullPointerException.class,
                () -> tokenGenerator.generate(
                        null,
                        UserSystemRole.USER
                )
        );
    }

    @Test
    void shouldRejectNullSystemRole() {
        assertThrows(
                NullPointerException.class,
                () -> tokenGenerator.generate(
                        USER_ID,
                        null
                )
        );
    }

    @Test
    void shouldRejectZeroTokenTtl() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new JwtAccessTokenGenerator(
                        jwtEncoder,
                        Clock.fixed(NOW, ZoneOffset.UTC),
                        Duration.ZERO
                )
        );
    }

    @Test
    void shouldRejectNegativeTokenTtl() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new JwtAccessTokenGenerator(
                        jwtEncoder,
                        Clock.fixed(NOW, ZoneOffset.UTC),
                        Duration.ofMinutes(-1)
                )
        );
    }

    private static final class TestJwtEncoder
            implements JwtEncoder {

        private JwtEncoderParameters parameters;

        @Override
        public Jwt encode(JwtEncoderParameters parameters) {
            this.parameters = parameters;

            return new Jwt(
                    "test-access-token",
                    NOW,
                    NOW.plus(TOKEN_TTL),
                    Map.of("alg", "HS256"),
                    Map.of(
                            "sub", USER_ID.toString(),
                            "systemRole", UserSystemRole.USER.name()
                    )
            );
        }
    }
}