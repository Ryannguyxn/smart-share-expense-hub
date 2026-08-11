package com.ryannguyxn.smartshareexpensehub.user.infrastructure.persistence.jpa;

import com.ryannguyxn.smartshareexpensehub.user.domain.Email;
import com.ryannguyxn.smartshareexpensehub.user.domain.User;
import com.ryannguyxn.smartshareexpensehub.user.domain.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaUserRepositoryAdapter implements UserRepository {

    private final SpringDataUserJpaRepository springDataRepository;

    public JpaUserRepositoryAdapter(SpringDataUserJpaRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public Optional<User> findById(UUID id) {
        return springDataRepository.findById(id)
                .map(UserJpaMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        return springDataRepository.findByEmail(email.value())
                .map(UserJpaMapper::toDomain);
    }

    @Override
    public User save(User user) {
        UserJpaEntity savedEntity = springDataRepository.save(UserJpaMapper.toEntity(user));
        return UserJpaMapper.toDomain(savedEntity);
    }
}