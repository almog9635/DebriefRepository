package org.example.debriefrepository.types;

import org.jetbrains.annotations.NotNull;

import java.util.Set;

public record GroupInput(
        String name,
        Long commander,
        Set<Long> users) {
}
