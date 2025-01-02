package org.example.debriefrepository.input;

import java.util.Set;

public record UserInput(
                        String firstName,
                        String lastName,
                        String password,
                        Set<RoleInput> roles,
                        GroupInput group,
                        String serviceType,
                        Set<MissionInput> missions) {
}
