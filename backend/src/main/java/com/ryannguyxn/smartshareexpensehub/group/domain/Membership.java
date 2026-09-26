package com.ryannguyxn.smartshareexpensehub.group.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public final class Membership {

    private final UUID id;
    private final UUID userId;
    private final UUID groupId;
    private MembershipRole role;
    private MembershipStatus status;
    private final Instant joinedAt;
    private Instant endedAt;

    public Membership(
            UUID id,
            UUID userId,
            UUID groupId,
            MembershipRole role,
            MembershipStatus status,
            Instant joinedAt,
            Instant endedAt
    ) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.userId = Objects.requireNonNull(userId, "userId must not be null");
        this.groupId = Objects.requireNonNull(groupId, "groupId must not be null");
        this.role = Objects.requireNonNull(role, "role must not be null");
        this.status = Objects.requireNonNull(status, "status must not be null");
        this.joinedAt = Objects.requireNonNull(joinedAt, "joinedAt must not be null");
        this.endedAt = endedAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getGroupId() {
        return groupId;
    }

    public MembershipRole getRole() {
        return role;
    }

    public MembershipStatus getStatus() {
        return status;
    }

    public Instant getJoinedAt() {
        return joinedAt;
    }

    public Instant getEndedAt() {
        return endedAt;
    }
}