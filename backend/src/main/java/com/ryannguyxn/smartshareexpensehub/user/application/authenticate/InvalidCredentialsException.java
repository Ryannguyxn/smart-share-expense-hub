package com.ryannguyxn.smartshareexpensehub.user.application.authenticate;

public final class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException() {
        super("Invalid email or password");
    }
}
