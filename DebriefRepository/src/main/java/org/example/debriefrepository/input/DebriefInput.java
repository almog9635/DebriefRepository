package org.example.debriefrepository.input;

import java.time.LocalDate;
import java.util.Date;
import java.util.Set;

public record DebriefInput(
                           String content,
                           LocalDate date,
                           UserInput user,
                           GroupInput group,
                           Set<LessonInput> lessons,
                           Set<MissionInput> missions) {
}
