package org.example.debriefrepository.types.update;

import org.example.debriefrepository.entity.Lesson;
import org.example.debriefrepository.entity.Mission;
import org.example.debriefrepository.types.input.GroupInput;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.Set;

public record DebriefUpdate(
        @NotNull Long id,
        String content,
        LocalDate date,
        Long userId,
        GroupInput group,
        Set<Lesson> lessons,
        Set<Mission> missions) {
}
