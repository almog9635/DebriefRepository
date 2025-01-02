package org.example.debriefrepository.input;

import java.util.Date;

public record MissionInput(
                           String content,
                           Date startDate,
                           Date deadline,
                           DebriefInput debriefInput,
                           UserInput userInput,
                           LessonInput lessonInput) {
}
