package org.example.debriefrepository.types;

import java.util.List;

public record GroupInput(
        String id,
        String name,
        String commander,
        List<Long> users) {
}
