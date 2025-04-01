package org.example.debriefrepository.types.input;

import java.time.ZonedDateTime;

public record TaskInput(
        String id,
        String content,
        ZonedDateTime startDate,
        ZonedDateTime deadline,
        String debrief,
        String user,
        String lesson) {
}
