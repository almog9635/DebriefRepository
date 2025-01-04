package org.example.debriefrepository.types.update;

import org.jetbrains.annotations.NotNull;

public record GroupUpdate(
        @NotNull Long id,
        @NotNull String name) {
}
