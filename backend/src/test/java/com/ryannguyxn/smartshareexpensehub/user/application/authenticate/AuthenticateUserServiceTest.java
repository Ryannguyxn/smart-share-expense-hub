package com.ryannguyxn.smartshareexpensehub.user.application.authenticate;

import com.ryannguyxn.smartshareexpensehub.user.application.port.AccessTokenGenerator;
import com.ryannguyxn.smartshareexpensehub.user.application.port.PasswordVerifier;
import com.ryannguyxn.smartshareexpensehub.user.domain.Email;
import com.ryannguyxn.smartshareexpensehub.user.domain.PasswordHash;
import com.ryannguyxn.smartshareexpensehub.user.domain.User;
import com.ryannguyxn.smartshareexpensehub.user.domain.UserRepository;
import com.ryannguyxn.smartshareexpensehub.user.domain.UserSystemRole;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AuthenticateUserServiceTest {

    private static final UUID USER_ID =
            UUID.fromString("11111111-1111-1111-1111-111111111111");

    private final InMemoryUserRepository userRepository =
            new InMemoryUserRepository();

    private final TestPasswordVerifier passwordVerifier =
            new TestPasswordVerifier();

    private final TestAccessTokenGenerator accessTokenGenerator =
            new TestAccessTokenGenerator();

    private final AuthenticateUserService authenticateUserService =
            new AuthenticateUserService(
                    userRepository,
                    passwordVerifier,
                    accessTokenGenerator
            );

    @Test
    void shouldAuthenticateUserWithValidCredentials() {
        User user = createUser();
        userRepository.save(user);

        passwordVerifier.shouldMatch = true;

        AuthenticateUserResult result =
                authenticateUserService.authenticate(
                        new AuthenticateUserCommand(
                                "member@example.test",
                                "correct-password"
                        )
                );

        assertEquals("test-access-token", result.accessToken());
        assertEquals(USER_ID, accessTokenGenerator.generatedForUserId);
        assertEquals(
                UserSystemRole.USER,
                accessTokenGenerator.generatedForSystemRole
        );
    }

    @Test
    void shouldRejectUnknownEmail() {
        assertThrows(
                InvalidCredentialsException.class,
                () -> authenticateUserService.authenticate(
                        new AuthenticateUserCommand(
                                "unknown@example.test",
                                "some-password"
                        )
                )
        );
    }

    @Test
    void shouldRejectIncorrectPassword() {
        userRepository.save(createUser());

        passwordVerifier.shouldMatch = false;

        assertThrows(
                InvalidCredentialsException.class,
                () -> authenticateUserService.authenticate(
                        new AuthenticateUserCommand(
                                "member@example.test",
                                "wrong-password"
                        )
                )
        );
    }

    @Test
    void shouldNotGenerateAccessTokenWhenAuthenticationFails() {
        userRepository.save(createUser());

        passwordVerifier.shouldMatch = false;

        assertThrows(
                InvalidCredentialsException.class,
                () -> authenticateUserService.authenticate(
                        new AuthenticateUserCommand(
                                "member@example.test",
                                "wrong-password"
                        )
                )
        );

        assertFalse(accessTokenGenerator.wasCalled);
    }

    @Test
    void shouldRejectNullCommand() {
        assertThrows(
                NullPointerException.class,
                () -> authenticateUserService.authenticate(null)
        );
    }

    private User createUser() {
        Instant now = Instant.parse("2026-09-02T00:00:00Z");

        return User.register(
                USER_ID,
                new Email("member@example.test"),
                new PasswordHash("stored-password-hash"),
                now
        );
    }

    private static final class InMemoryUserRepository
            implements UserRepository {

        private final Map<Email, User> usersByEmail = new HashMap<>();

        @Override
        public Optional<User> findById(UUID id) {
            return usersByEmail.values()
                    .stream()
                    .filter(user -> user.id().equals(id))
                    .findFirst();
        }

        @Override
        public Optional<User> findByEmail(Email email) {
            return Optional.ofNullable(usersByEmail.get(email));
        }

        @Override
        public User save(User user) {
            usersByEmail.put(user.email(), user);
            return user;
        }
    }

    private static final class TestPasswordVerifier
            implements PasswordVerifier {

        private boolean shouldMatch;

        @Override
        public boolean matches(
                String rawPassword,
                PasswordHash passwordHash
        ) {
            return shouldMatch;
        }
    }

    private static final class TestAccessTokenGenerator
            implements AccessTokenGenerator {

        private boolean wasCalled;
        private UUID generatedForUserId;
        private UserSystemRole generatedForSystemRole;

        @Override
        public String generate(
                UUID userId,
                UserSystemRole systemRole
        ) {
            wasCalled = true;
            generatedForUserId = userId;
            generatedForSystemRole = systemRole;

            return "test-access-token";
        }
    }
}