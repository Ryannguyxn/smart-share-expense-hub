package com.ryannguyxn.smartshareexpensehub.user.application.port;

import com.ryannguyxn.smartshareexpensehub.user.domain.PasswordHash;

public interface PasswordVerifier {
    boolean matches(String rawPassword, PasswordHash passwordHash);
}
