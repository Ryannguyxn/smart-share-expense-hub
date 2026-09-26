package com.ryannguyxn.smartshareexpensehub.group.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GroupTest {

    @Test
    void shouldCreateActiveGroupWithCreatorAsOwner() {
        UUID groupId = UUID.randomUUID();
        UUID creatorUserId = UUID.randomUUID();
        UUID creatorMembershipId = UUID.randomUUID();
        Instant createdAt = Instant.parse("2026-09-25T00:00:00Z");

        Group group = Group.create(
                groupId,
                "Da Lat Trip",
                new CurrencyCode("VND"),
                creatorUserId,
                creatorMembershipId,
                createdAt
        );

        assertEquals(groupId, group.getId());
        assertEquals("Da Lat Trip", group.getName());
        assertEquals(new CurrencyCode("VND"), group.getCurrencyCode());
        assertEquals(GroupStatus.ACTIVE, group.getStatus());
        assertNull(group.getArchivedAt());

        assertEquals(1, group.getMemberships().size());

        Membership owner = group.getMemberships().getFirst();

        assertEquals(creatorMembershipId, owner.getId());
        assertEquals(creatorUserId, owner.getUserId());
        assertEquals(groupId, owner.getGroupId());
        assertEquals(MembershipRole.OWNER, owner.getRole());
        assertEquals(MembershipStatus.ACTIVE, owner.getStatus());
        assertEquals(createdAt, owner.getJoinedAt());
        assertNull(owner.getEndedAt());
    }

    @Test
    void shouldTrimGroupName() {
        Group group = Group.create(
                UUID.randomUUID(),
                "  Da Lat Trip  ",
                new CurrencyCode("VND"),
                UUID.randomUUID(),
                UUID.randomUUID(),
                Instant.now()
        );

        assertEquals("Da Lat Trip", group.getName());
    }

    @Test
    void shouldRejectBlankGroupName() {
        assertThrows(
                IllegalArgumentException.class,
                () -> Group.create(
                        UUID.randomUUID(),
                        "   ",
                        new CurrencyCode("VND"),
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        Instant.now()
                )
        );
    }

    @Test
    void shouldRejectGroupNameLongerThanOneHundredCharacters() {
        String name = "a".repeat(101);

        assertThrows(
                IllegalArgumentException.class,
                () -> Group.create(
                        UUID.randomUUID(),
                        name,
                        new CurrencyCode("VND"),
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        Instant.now()
                )
        );
    }

    @Test
    void shouldNotAllowMembershipListToBeModifiedExternally() {
        Group group = Group.create(
                UUID.randomUUID(),
                "Da Lat Trip",
                new CurrencyCode("VND"),
                UUID.randomUUID(),
                UUID.randomUUID(),
                Instant.now()
        );

        assertThrows(
                UnsupportedOperationException.class,
                () -> group.getMemberships().clear()
        );
    }
}