package com.ryannguyxn.smartshareexpensehub.user.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public final class User {

    private final UUID id;
    private final Email email;
    private final PasswordHash passwordHash;
    private final UserSystemRole systemRole;
    private final Instant createdAt;
    private final Instant updatedAt;

    private User(
            UUID id,
            Email email,
            PasswordHash passwordHash,
            UserSystemRole systemRole,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = Objects.requireNonNull(id, "User id must not be null");
        this.email = Objects.requireNonNull(email, "Email must not be null");
        this.passwordHash = Objects.requireNonNull(passwordHash, "Password hash must not be null");
        this.systemRole = Objects.requireNonNull(systemRole, "System role must not be null");
        this.createdAt = Objects.requireNonNull(createdAt, "Created time must not be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "Updated time must not be null");

        if (updatedAt.isBefore(createdAt)) {
            throw new IllegalArgumentException("Updated time must not be before created time");
        }
    }

    public static User register(UUID id, Email email, PasswordHash passwordHash, Instant now) {
        return new User(id, email, passwordHash, UserSystemRole.USER, now, now);
    }

    public static User reconstitute(
            UUID id,
            Email email,
            PasswordHash passwordHash,
            UserSystemRole systemRole,
            Instant createdAt,
            Instant updatedAt
    ) {
        return new User(id, email, passwordHash, systemRole, createdAt, updatedAt);
    }

    public UUID id() {
        return id;
    }

    public Email email() {
        return email;
    }

    public PasswordHash passwordHash() {
        return passwordHash;
    }

    public UserSystemRole systemRole() {
        return systemRole;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }

    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof User user && id.equals(user.id));
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}