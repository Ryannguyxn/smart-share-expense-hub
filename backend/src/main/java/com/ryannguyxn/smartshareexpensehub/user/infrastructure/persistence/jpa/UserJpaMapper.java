package com.ryannguyxn.smartshareexpensehub.user.infrastructure.persistence.jpa;

import com.ryannguyxn.smartshareexpensehub.user.domain.Email;
import com.ryannguyxn.smartshareexpensehub.user.domain.PasswordHash;
import com.ryannguyxn.smartshareexpensehub.user.domain.User;

import java.util.Objects;

public final class UserJpaMapper {

    private UserJpaMapper() {
    }

    public static UserJpaEntity toEntity(User user) {
        Objects.requireNonNull(user, "User must not be null");

        return new UserJpaEntity(
                user.id(),
                user.email().value(),
                user.passwordHash().value(),
                user.systemRole(),
                user.createdAt(),
                user.updatedAt()
        );
    }

    public static User toDomain(UserJpaEntity entity) {
        Objects.requireNonNull(entity, "User persistence entity must not be null");

        return User.reconstitute(
                entity.id(),
                new Email(entity.email()),
                new PasswordHash(entity.passwordHash()),
                entity.systemRole(),
                entity.createdAt(),
                entity.updatedAt()
        );
    }
}