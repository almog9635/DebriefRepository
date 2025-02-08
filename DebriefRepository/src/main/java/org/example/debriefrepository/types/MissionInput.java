package org.example.debriefrepository.types;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.ZonedDateTime;

public record MissionInput(
        String id,
        String content,
        ZonedDateTime startDate,
        ZonedDateTime deadline,
        Long debrief,
        Long user,
        Long lessonInput) {
}
