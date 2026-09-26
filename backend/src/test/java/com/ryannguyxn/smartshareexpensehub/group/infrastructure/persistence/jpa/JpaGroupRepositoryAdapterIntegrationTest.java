package com.ryannguyxn.smartshareexpensehub.group.infrastructure.persistence.jpa;

import com.ryannguyxn.smartshareexpensehub.group.domain.CurrencyCode;
import com.ryannguyxn.smartshareexpensehub.group.domain.Group;
import com.ryannguyxn.smartshareexpensehub.group.domain.GroupRepository;
import com.ryannguyxn.smartshareexpensehub.group.domain.GroupStatus;
import com.ryannguyxn.smartshareexpensehub.group.domain.MembershipRole;
import com.ryannguyxn.smartshareexpensehub.group.domain.MembershipStatus;
import com.ryannguyxn.smartshareexpensehub.user.domain.Email;
import com.ryannguyxn.smartshareexpensehub.user.domain.PasswordHash;
import com.ryannguyxn.smartshareexpensehub.user.domain.User;
import com.ryannguyxn.smartshareexpensehub.user.domain.UserRepository;
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
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
class JpaGroupRepositoryAdapterIntegrationTest {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserRepository userRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void shouldPersistAndReloadGroupWithOwnerMembership() {
        UUID userId = UUID.randomUUID();
        Instant userCreatedAt = Instant.parse("2026-09-25T08:00:00Z");

        User creator = User.register(
                userId,
                new Email("group-creator-" + userId + "@example.test"),
                new PasswordHash("hashed-password-value"),
                userCreatedAt
        );

        userRepository.save(creator);

        UUID groupId = UUID.randomUUID();
        UUID membershipId = UUID.randomUUID();
        Instant groupCreatedAt = Instant.parse("2026-09-25T09:00:00Z");

        Group group = Group.create(
                groupId,
                "Da Lat Trip",
                new CurrencyCode("VND"),
                userId,
                membershipId,
                groupCreatedAt
        );

        groupRepository.save(group);

        entityManager.flush();
        entityManager.clear();

        Group reloadedGroup = groupRepository.findById(groupId)
                .orElseThrow();

        assertAll(
                () -> assertEquals(groupId, reloadedGroup.getId()),
                () -> assertEquals("Da Lat Trip", reloadedGroup.getName()),
                () -> assertEquals(new CurrencyCode("VND"), reloadedGroup.getCurrencyCode()),
                () -> assertEquals(GroupStatus.ACTIVE, reloadedGroup.getStatus()),
                () -> assertNull(reloadedGroup.getArchivedAt()),
                () -> assertEquals(1, reloadedGroup.getMemberships().size())
        );

        var owner = reloadedGroup.getMemberships().getFirst();

        assertAll(
                () -> assertEquals(membershipId, owner.getId()),
                () -> assertEquals(userId, owner.getUserId()),
                () -> assertEquals(groupId, owner.getGroupId()),
                () -> assertEquals(MembershipRole.OWNER, owner.getRole()),
                () -> assertEquals(MembershipStatus.ACTIVE, owner.getStatus()),
                () -> assertEquals(groupCreatedAt, owner.getJoinedAt()),
                () -> assertNull(owner.getEndedAt())
        );
    }
}