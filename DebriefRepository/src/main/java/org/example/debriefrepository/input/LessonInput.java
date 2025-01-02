package org.example.debriefrepository.input;

import java.util.Set;

public record LessonInput(Long id,
                          String content,
                          DebriefInput debrief,
                          Set<MissionInput> missions) {
}
