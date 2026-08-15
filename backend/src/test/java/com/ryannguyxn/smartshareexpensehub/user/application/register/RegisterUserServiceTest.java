package com.ryannguyxn.smartshareexpensehub.user.application.register;

import com.ryannguyxn.smartshareexpensehub.user.application.port.PasswordHashGenerator;
import com.ryannguyxn.smartshareexpensehub.user.domain.Email;
import com.ryannguyxn.smartshareexpensehub.user.domain.PasswordHash;
import com.ryannguyxn.smartshareexpensehub.user.domain.User;
import com.ryannguyxn.smartshareexpensehub.user.domain.UserRepository;
import com.ryannguyxn.smartshareexpensehub.user.domain.UserSystemRole;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RegisterUserServiceTest {

    private final InMemoryUserRepository userRepository = new InMemoryUserRepository();

    private final PasswordHashGenerator passwordHashGenerator =
            rawPassword -> new PasswordHash("test-hash-" + rawPassword);

    private final Clock clock = Clock.fixed(
            Instant.parse("2026-08-15T00:00:00Z"),
            ZoneOffset.UTC
    );

    private final RegisterUserService registerUserService =
            new RegisterUserService(userRepository, passwordHashGenerator, clock);

    @Test
    void shouldRegisterUser() {
        RegisterUserResult result = registerUserService.register(
                new RegisterUserCommand("member@example.test", "raw-password")
        );

        User persistedUser = userRepository.findById(result.userId()).orElseThrow();

        assertAll(
                () -> assertEquals("member@example.test", persistedUser.email().value()),
                () -> assertEquals("test-hash-raw-password", persistedUser.passwordHash().value()),
                () -> assertEquals(UserSystemRole.USER, persistedUser.systemRole()),
                () -> assertEquals(Instant.parse("2026-08-15T00:00:00Z"), persistedUser.createdAt()),
                () -> assertEquals(Instant.parse("2026-08-15T00:00:00Z"), persistedUser.updatedAt())
        );
    }

    @Test
    void shouldRejectAlreadyRegisteredEmail() {
        RegisterUserCommand command =
                new RegisterUserCommand("duplicate@example.test", "raw-password");

        registerUserService.register(command);

        assertThrows(
                EmailAlreadyRegisteredException.class,
                () -> registerUserService.register(command)
        );
    }

    private static final class InMemoryUserRepository implements UserRepository {

        private final Map<UUID, User> usersById = new HashMap<>();
        private final Map<Email, User> usersByEmail = new HashMap<>();

        @Override
        public Optional<User> findById(UUID id) {
            return Optional.ofNullable(usersById.get(id));
        }

        @Override
        public Optional<User> findByEmail(Email email) {
            return Optional.ofNullable(usersByEmail.get(email));
        }

        @Override
        public User save(User user) {
            usersById.put(user.id(), user);
            usersByEmail.put(user.email(), user);
            return user;
        }
    }
}