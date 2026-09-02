package com.ryannguyxn.smartshareexpensehub.user.application.authenticate;

public interface AuthenticateUserUseCase {
    AuthenticateUserResult authenticate(AuthenticateUserCommand command);
}
