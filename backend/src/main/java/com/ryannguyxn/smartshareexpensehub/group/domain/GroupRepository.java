package com.ryannguyxn.smartshareexpensehub.group.domain;

import java.util.Optional;
import java.util.UUID;

public interface GroupRepository {

    Group save(Group group);

    Optional<Group> findById(UUID groupId);
}