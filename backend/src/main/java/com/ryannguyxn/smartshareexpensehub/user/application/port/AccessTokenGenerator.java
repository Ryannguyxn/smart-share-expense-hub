package com.ryannguyxn.smartshareexpensehub.user.application.port;

import com.ryannguyxn.smartshareexpensehub.user.domain.UserSystemRole;

import java.util.UUID;

public interface AccessTokenGenerator {
    String generate(UUID userId, UserSystemRole systemRole);
}
