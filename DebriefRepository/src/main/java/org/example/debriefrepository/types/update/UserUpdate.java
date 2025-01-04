package org.example.debriefrepository.types.update;

import org.example.debriefrepository.types.input.GroupInput;
import org.example.debriefrepository.types.input.MissionInput;
import org.example.debriefrepository.types.input.RoleInput;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public record UserUpdate(@NotNull Long id,
                         String firstName,
                         String lastName,
                         String password,
                         Set<RoleInput> roles,
                         GroupInput group,
                         String serviceType,
                         Set<MissionUpdate> missions) {
}
