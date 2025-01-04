package org.example.debriefrepository.types.update;

import org.example.debriefrepository.types.input.DebriefInput;
import org.example.debriefrepository.types.input.LessonInput;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;

public record MissionUpdate(
        @NotNull Long id,
        String content,
        @NotNull LocalDate startDate,
        @NotNull LocalDate deadline,
        @NotNull Long debriefId,
        @NotNull Long userId,
        LessonInput lessonInput) {
}
