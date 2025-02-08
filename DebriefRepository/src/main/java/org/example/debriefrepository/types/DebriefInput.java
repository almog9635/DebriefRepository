package org.example.debriefrepository.types;

import java.time.ZonedDateTime;
import java.util.List;

public record DebriefInput(
        String id,
        String content,
        ZonedDateTime date,
        String userId,
        String group,
        List<Long> lessons,
        List<Long> missions) {
}
