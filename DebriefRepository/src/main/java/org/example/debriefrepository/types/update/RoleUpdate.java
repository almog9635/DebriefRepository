package org.example.debriefrepository.types.update;

import org.jetbrains.annotations.NotNull;

public record RoleUpdate(@NotNull Long id,
                         @NotNull String roleName) {
}
