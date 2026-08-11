package com.ryannguyxn.smartshareexpensehub.user.infrastructure.persistence.jpa;

import com.ryannguyxn.smartshareexpensehub.user.domain.Email;
import com.ryannguyxn.smartshareexpensehub.user.domain.PasswordHash;
import com.ryannguyxn.smartshareexpensehub.user.domain.User;
import com.ryannguyxn.smartshareexpensehub.user.domain.UserRepository;
import com.ryannguyxn.smartshareexpensehub.user.domain.UserSystemRole;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
class JpaUserRepositoryAdapterIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void shouldPersistAndReloadUserThroughRepositoryPort() {
        UUID id = UUID.randomUUID();
        Email email = new Email("persistence-" + id + "@example.test");
        Instant now = Instant.parse("2026-08-10T12:00:00Z");

        User user = User.register(
                id,
                email,
                new PasswordHash("hashed-password-value"),
                now
        );

        userRepository.save(user);

        entityManager.flush();
        entityManager.clear();

        User reloadedUser = userRepository.findByEmail(email)
                .orElseThrow();

        assertAll(
                () -> assertEquals(id, reloadedUser.id()),
                () -> assertEquals(email, reloadedUser.email()),
                () -> assertEquals(UserSystemRole.USER, reloadedUser.systemRole()),
                () -> assertEquals(now, reloadedUser.createdAt()),
                () -> assertEquals(now, reloadedUser.updatedAt())
        );
    }
}