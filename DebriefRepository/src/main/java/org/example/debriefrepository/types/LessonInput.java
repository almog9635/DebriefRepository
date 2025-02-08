package org.example.debriefrepository.types;

import java.util.List;

public record LessonInput(
        String id,
        String content,
        String debriefId,
        List<Long> missions) {
}
