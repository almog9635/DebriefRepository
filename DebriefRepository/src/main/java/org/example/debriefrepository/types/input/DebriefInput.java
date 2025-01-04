package org.example.debriefrepository.types.input;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.Set;

public record DebriefInput(
        @NotNull String content,
        @NotNull LocalDate date,
        @NotNull UserInput user,
        @NotNull GroupInput group,
        @NotNull Set<LessonInput> lessons,
        @NotNull Set<MissionInput> missions) {
}
