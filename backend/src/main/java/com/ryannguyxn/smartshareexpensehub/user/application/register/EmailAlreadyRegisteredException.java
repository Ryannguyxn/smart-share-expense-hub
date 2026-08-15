package com.ryannguyxn.smartshareexpensehub.user.application.register;

public final class EmailAlreadyRegisteredException extends RuntimeException {

    public EmailAlreadyRegisteredException(String email) {
        super("A user with email '%s' already exists".formatted(email));
    }
}