package com.ryannguyxn.smartshareexpensehub.group.domain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class Group {

    private final UUID id;
    private String name;
    private final CurrencyCode currencyCode;
    private GroupStatus status;
    private Instant archivedAt;

    private final List<Membership> memberships;

    private Group(
            UUID id,
            String name,
            CurrencyCode currencyCode,
            GroupStatus status,
            Instant archivedAt,
            List<Membership> memberships
    ) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.name = validateName(name);
        this.currencyCode = Objects.requireNonNull(
                currencyCode,
                "currencyCode must not be null"
        );
        this.status = Objects.requireNonNull(
                status,
                "status must not be null"
        );
        this.memberships = new ArrayList<>(
                Objects.requireNonNull(
                        memberships,
                        "memberships must not be null"
                )
        );

        validateLifecycle(status, archivedAt);

        this.archivedAt = archivedAt;
    }

    public static Group create(
            UUID groupId,
            String name,
            CurrencyCode currencyCode,
            UUID creatorUserId,
            UUID creatorMembershipId,
            Instant createdAt
    ) {
        Objects.requireNonNull(
                creatorUserId,
                "creatorUserId must not be null"
        );

        Objects.requireNonNull(
                creatorMembershipId,
                "creatorMembershipId must not be null"
        );

        Objects.requireNonNull(
                createdAt,
                "createdAt must not be null"
        );

        Membership ownerMembership = new Membership(
                creatorMembershipId,
                creatorUserId,
                groupId,
                MembershipRole.OWNER,
                MembershipStatus.ACTIVE,
                createdAt,
                null
        );

        return new Group(
                groupId,
                name,
                currencyCode,
                GroupStatus.ACTIVE,
                null,
                List.of(ownerMembership)
        );
    }

    public static Group rehydrate(
            UUID groupId,
            String name,
            CurrencyCode currencyCode,
            GroupStatus status,
            Instant archivedAt,
            List<Membership> memberships
    ) {
        return new Group(
                groupId,
                name,
                currencyCode,
                status,
                archivedAt,
                memberships
        );
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public CurrencyCode getCurrencyCode() {
        return currencyCode;
    }

    public GroupStatus getStatus() {
        return status;
    }

    public Instant getArchivedAt() {
        return archivedAt;
    }

    public List<Membership> getMemberships() {
        return List.copyOf(memberships);
    }

    private static String validateName(String name) {
        Objects.requireNonNull(name, "name must not be null");

        String normalizedName = name.trim();

        if (normalizedName.isBlank()) {
            throw new IllegalArgumentException(
                    "name must not be blank"
            );
        }

        if (normalizedName.length() > 100) {
            throw new IllegalArgumentException(
                    "name must not exceed 100 characters"
            );
        }

        return normalizedName;
    }

    private static void validateLifecycle(
            GroupStatus status,
            Instant archivedAt
    ) {
        if (status == GroupStatus.ACTIVE && archivedAt != null) {
            throw new IllegalArgumentException(
                    "active group must not have archivedAt"
            );
        }

        if (status == GroupStatus.ARCHIVED && archivedAt == null) {
            throw new IllegalArgumentException(
                    "archived group must have archivedAt"
            );
        }
    }
}