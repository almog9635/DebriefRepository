package org.example.debriefrepository.types;

import java.util.List;
import java.util.Set;

public record UserInput(
        String id,
        String firstName,
        String lastName,
        String password,
        List<RoleInput> roles,
        String rank,
        String group,
        String serviceType,
        List<Long> missions) {
}
