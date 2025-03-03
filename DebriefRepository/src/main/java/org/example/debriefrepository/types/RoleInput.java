package org.example.debriefrepository.types;

import org.jetbrains.annotations.NotNull;

public record RoleInput(
        @NotNull String id,
        String name) {
}
