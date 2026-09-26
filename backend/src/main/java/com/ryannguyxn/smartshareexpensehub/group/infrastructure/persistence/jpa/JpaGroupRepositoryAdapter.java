package com.ryannguyxn.smartshareexpensehub.group.infrastructure.persistence.jpa;

import com.ryannguyxn.smartshareexpensehub.group.domain.Group;
import com.ryannguyxn.smartshareexpensehub.group.domain.GroupRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaGroupRepositoryAdapter implements GroupRepository {

    private final SpringDataGroupJpaRepository groupRepository;
    private final SpringDataMembershipJpaRepository membershipRepository;
    private final GroupJpaMapper mapper;
    private final Clock clock;

    public JpaGroupRepositoryAdapter(
            SpringDataGroupJpaRepository groupRepository,
            SpringDataMembershipJpaRepository membershipRepository,
            GroupJpaMapper mapper,
            Clock clock
    ) {
        this.groupRepository = groupRepository;
        this.membershipRepository = membershipRepository;
        this.mapper = mapper;
        this.clock = clock;
    }

    @Override
    @Transactional
    public Group save(Group group) {
        Instant now = clock.instant();

        GroupJpaEntity groupEntity =
                mapper.toGroupEntity(group, now);

        List<MembershipJpaEntity> membershipEntities =
                mapper.toMembershipEntities(group, now);

        groupRepository.save(groupEntity);
        membershipRepository.saveAll(membershipEntities);

        return group;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Group> findById(UUID groupId) {
        return groupRepository.findById(groupId)
                .map(groupEntity -> {
                    List<MembershipJpaEntity> memberships =
                            membershipRepository.findByGroupId(groupId);

                    return mapper.toDomain(
                            groupEntity,
                            memberships
                    );
                });
    }
}