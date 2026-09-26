package com.ryannguyxn.smartshareexpensehub.group.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringDataGroupJpaRepository
        extends JpaRepository<GroupJpaEntity, UUID> {
}