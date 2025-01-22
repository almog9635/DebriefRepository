package org.example.debriefrepository.types;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;

public record MissionInput(
        String content,
        LocalDate startDate,
        LocalDate deadline,
        Long debrief,
        Long user,
        Long lessonInput) {
}
