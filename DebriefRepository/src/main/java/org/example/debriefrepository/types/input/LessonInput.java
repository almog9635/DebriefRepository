package org.example.debriefrepository.types.input;

import java.util.List;

public record LessonInput(
        String id,
        String content,
        String debrief,
        List<TaskInput> tasks) {
}
