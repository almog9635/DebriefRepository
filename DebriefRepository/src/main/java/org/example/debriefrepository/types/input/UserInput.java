package org.example.debriefrepository.types.input;

import org.jetbrains.annotations.NotNull;

import java.util.Set;

public record UserInput(
        @NotNull String firstName,
        @NotNull String lastName,
        @NotNull String password,
        @NotNull Set<RoleInput> roles,
        @NotNull GroupInput group,
        @NotNull String serviceType) {
}
