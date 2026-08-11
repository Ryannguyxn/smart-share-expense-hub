package com.ryannguyxn.smartshareexpensehub.user.domain;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    Optional<User> findById(UUID id);

    Optional<User> findByEmail(Email email);

    User save(User user);
}