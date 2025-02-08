package org.example.debriefrepository.types;

import org.jetbrains.annotations.NotNull;

public record RoleInput(
        String id,
        @NotNull String name) {
}
