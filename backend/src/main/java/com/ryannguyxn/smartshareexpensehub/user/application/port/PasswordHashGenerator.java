package com.ryannguyxn.smartshareexpensehub.user.application.port;

import com.ryannguyxn.smartshareexpensehub.user.domain.PasswordHash;

public interface PasswordHashGenerator {

    PasswordHash hash(String rawPassword);
}