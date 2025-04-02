package org.example.debriefrepository.types.input;

import org.example.debriefrepository.types.enums.RankEnum;
import org.example.debriefrepository.types.enums.ServiceTypeEnum;

import java.util.List;

public record UserInput(
        String id,
        String firstName,
        String lastName,
        String password,
        List<String> roles,
        RankEnum rank,
        String group,
        ServiceTypeEnum serviceType,
        List<String> missions) {
}
