package org.example.debriefrepository.types.input;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;

public record MissionInput(
        @NotNull String content,
        @NotNull LocalDate startDate,
        @NotNull LocalDate deadline,
        @NotNull DebriefInput debriefInput,
        @NotNull Long userId,
        LessonInput lessonInput) {
}
