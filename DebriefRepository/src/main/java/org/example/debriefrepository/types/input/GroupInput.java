package org.example.debriefrepository.types.input;

import java.util.List;

public record GroupInput(
        String id,
        String name,
        String commander,
        List<String> users) {
}
