package com.ryannguyxn.smartshareexpensehub.user.application.authenticate;

import com.ryannguyxn.smartshareexpensehub.user.application.port.AccessTokenGenerator;
import com.ryannguyxn.smartshareexpensehub.user.application.port.PasswordVerifier;
import com.ryannguyxn.smartshareexpensehub.user.domain.Email;
import com.ryannguyxn.smartshareexpensehub.user.domain.User;
import com.ryannguyxn.smartshareexpensehub.user.domain.UserRepository;

import java.util.Objects;

public final class AuthenticateUserService implements AuthenticateUserUseCase {

    private final UserRepository userRepository;
    private final PasswordVerifier passwordVerifier;
    private final AccessTokenGenerator accessTokenGenerator;

    public AuthenticateUserService(
            UserRepository userRepository,
            PasswordVerifier passwordVerifier,
            AccessTokenGenerator accessTokenGenerator
    ) {
        this.userRepository = Objects.requireNonNull(
                userRepository,
                "userRepository must not be null"
        );
        this.passwordVerifier = Objects.requireNonNull(
                passwordVerifier,
                "passwordVerifier must not be null"
        );
        this.accessTokenGenerator = Objects.requireNonNull(
                accessTokenGenerator,
                "accessTokenGenerator must not be null"
        );
    }

    @Override
    public AuthenticateUserResult authenticate(AuthenticateUserCommand command) {
        Objects.requireNonNull(command, "command must not be null");

        Email email = new Email(command.email());

        User user = userRepository.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);

        boolean passwordMatches = passwordVerifier.matches(
                command.rawPassword(),
                user.passwordHash()
        );

        if (!passwordMatches) {
            throw new InvalidCredentialsException();
        }

        String accessToken = accessTokenGenerator.generate(
                user.id(),
                user.systemRole()
        );

        return new AuthenticateUserResult(accessToken);
    }
}
