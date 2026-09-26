package com.ryannguyxn.smartshareexpensehub.group.infrastructure.persistence.jpa;

import com.ryannguyxn.smartshareexpensehub.group.domain.CurrencyCode;
import com.ryannguyxn.smartshareexpensehub.group.domain.Group;
import com.ryannguyxn.smartshareexpensehub.group.domain.Membership;

import java.time.Instant;
import java.util.List;

public final class GroupJpaMapper {

    public GroupJpaEntity toGroupEntity(Group group, Instant now) {
        return new GroupJpaEntity(
                group.getId(),
                group.getName(),
                group.getCurrencyCode().value(),
                group.getStatus(),
                group.getArchivedAt(),
                now,
                now
        );
    }

    public List<MembershipJpaEntity> toMembershipEntities(
            Group group,
            Instant now
    ) {
        return group.getMemberships()
                .stream()
                .map(membership -> toMembershipEntity(membership, now))
                .toList();
    }

    private MembershipJpaEntity toMembershipEntity(
            Membership membership,
            Instant now
    ) {
        return new MembershipJpaEntity(
                membership.getId(),
                membership.getUserId(),
                membership.getGroupId(),
                membership.getRole(),
                membership.getStatus(),
                membership.getJoinedAt(),
                membership.getEndedAt(),
                now,
                now
        );
    }

    public Group toDomain(
            GroupJpaEntity groupEntity,
            List<MembershipJpaEntity> membershipEntities
    ) {
        List<Membership> memberships = membershipEntities
                .stream()
                .map(this::toDomainMembership)
                .toList();

        return Group.rehydrate(
                groupEntity.getId(),
                groupEntity.getName(),
                new CurrencyCode(groupEntity.getCurrencyCode()),
                groupEntity.getStatus(),
                groupEntity.getArchivedAt(),
                memberships
        );
    }

    private Membership toDomainMembership(
            MembershipJpaEntity entity
    ) {
        return new Membership(
                entity.getId(),
                entity.getUserId(),
                entity.getGroupId(),
                entity.getRole(),
                entity.getStatus(),
                entity.getJoinedAt(),
                entity.getEndedAt()
        );
    }
}