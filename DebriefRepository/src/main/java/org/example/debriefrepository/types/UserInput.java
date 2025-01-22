package org.example.debriefrepository.types;

import java.util.Set;

public record UserInput(
        String firstName,
        String lastName,
        String password,
        Set<RoleInput> roles,
        String rank,
        String group,
        String serviceType,
        Set<Long> missions) {
}
