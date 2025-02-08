package org.example.debriefrepository.types;

import java.util.List;

public record GroupInput(
        String id,
        String name,
        String commanderId,
        List<Long> users) {
}
