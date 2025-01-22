package org.example.debriefrepository.types;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.Set;

public record DebriefInput(
        String content,
        LocalDate date,
        Long userId,
        String group,
        Set<Long> lessons,
        Set<Long> missions) {
}
