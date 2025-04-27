package org.example.debriefrepository.types.input;

import org.example.debriefrepository.types.content.ContentInput;

import java.time.ZonedDateTime;
import java.util.List;

public record DebriefInput(
        String id,
        String title,
        String labels,
        ContentInput contentItems,
        ZonedDateTime date,
        List<LessonInput> lessons,
        List<TaskInput> tasks) {
}