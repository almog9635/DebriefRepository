package org.example.debriefrepository.types.input;

import org.jetbrains.annotations.NotNull;

import java.util.Set;

public record LessonInput(
        @NotNull Long id,
        @NotNull String content,
        @NotNull DebriefInput debrief,
        @NotNull Set<MissionInput> missions) {
}
