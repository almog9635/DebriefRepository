package org.example.debriefrepository.types.input;

import java.time.ZonedDateTime;

public record MissionInput(
        String id,
        String content,
        ZonedDateTime startDate,
        ZonedDateTime deadline,
        String debriefId,
        String user,
        String lessonId) {
}
