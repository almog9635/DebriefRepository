package org.example.debriefrepository.types;

import org.jetbrains.annotations.NotNull;

import java.util.Set;

public record LessonInput(
        String content,
        Long debriefId,
        Set<Long> missions) {
}
