package org.example.debriefrepository.types;

import java.util.List;
import java.util.Set;

public record UserInput(
        String id,
        String firstName,
        String lastName,
        String password,
        List<String> roles,
        Rank rank,
        String group,
        ServiceType serviceType,
        List<String> missions) {
}
