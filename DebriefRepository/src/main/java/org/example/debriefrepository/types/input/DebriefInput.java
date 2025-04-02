package org.example.debriefrepository.types.input;

import org.example.debriefrepository.types.content.ContentInput;

import java.time.ZonedDateTime;
import java.util.List;

public record DebriefInput(
        String id,
        ContentInput contentItems,
        ZonedDateTime date,
        String user,
        String group,
        List<LessonInput> lessons,
        List<TaskInput> tasks) {
}