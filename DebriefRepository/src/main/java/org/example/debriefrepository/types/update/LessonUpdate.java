package org.example.debriefrepository.types.update;

import org.example.debriefrepository.types.input.DebriefInput;
import org.example.debriefrepository.types.input.MissionInput;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public record LessonUpdate(
        @NotNull Long id,
        String content,
        @NotNull Long debriefId,
        Set<MissionInput> missions) {
}
