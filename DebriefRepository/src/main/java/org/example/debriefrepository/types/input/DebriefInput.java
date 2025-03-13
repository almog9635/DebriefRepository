package org.example.debriefrepository.types.input;

import org.example.debriefrepository.types.content.ContentItemInput;

import java.time.ZonedDateTime;
import java.util.List;

public record DebriefInput(
        String id,
        List<ContentItemInput> contentItems,
        ZonedDateTime date,
        String user,
        String group,
        List<String> lessons,
        List<String> missions) {
}