package com.ryannguyxn.smartshareexpensehub.user.application.register;

import com.ryannguyxn.smartshareexpensehub.user.application.port.PasswordHashGenerator;
import com.ryannguyxn.smartshareexpensehub.user.domain.Email;
import com.ryannguyxn.smartshareexpensehub.user.domain.PasswordHash;
import com.ryannguyxn.smartshareexpensehub.user.domain.User;
import com.ryannguyxn.smartshareexpensehub.user.domain.UserRepository;

import java.time.Clock;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public final class RegisterUserService implements RegisterUserUseCase {

    private final UserRepository userRepository;
    private final PasswordHashGenerator passwordHashGenerator;
    private final Clock clock;

    public RegisterUserService(
            UserRepository userRepository,
            PasswordHashGenerator passwordHashGenerator,
            Clock clock
    ) {
        this.userRepository = Objects.requireNonNull(userRepository, "userRepository must not be null");
        this.passwordHashGenerator = Objects.requireNonNull(
                passwordHashGenerator,
                "passwordHashGenerator must not be null"
        );
        this.clock = Objects.requireNonNull(clock, "clock must not be null");
    }

    @Override
    public RegisterUserResult register(RegisterUserCommand command) {
        Objects.requireNonNull(command, "command must not be null");

        Email email = new Email(command.email());

        if (userRepository.findByEmail(email).isPresent()) {
            throw new EmailAlreadyRegisteredException(email.value());
        }

        PasswordHash passwordHash = passwordHashGenerator.hash(command.rawPassword());
        Instant now = clock.instant();

        User savedUser = userRepository.save(
                User.register(UUID.randomUUID(), email, passwordHash, now)
        );

        return new RegisterUserResult(savedUser.id());
    }
}