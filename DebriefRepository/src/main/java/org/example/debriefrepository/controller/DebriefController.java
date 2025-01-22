package org.example.debriefrepository.controller;

import lombok.RequiredArgsConstructor;
import org.example.debriefrepository.entity.Debrief;
import org.example.debriefrepository.service.DebriefService;
import org.example.debriefrepository.types.DebriefInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class DebriefController {

    @Autowired
    private final DebriefService debriefService;

    @QueryMapping
    public List<Debrief> debriefs(@Argument("input") Map<String, Object> input) {
        return debriefService.getDebriefs(input);
    }

    @QueryMapping
    public List<Debrief> getAllDebriefs() {
        return debriefService.getAllDebriefs();
    }

    @MutationMapping
    public Debrief addDebrief(@Argument("input") DebriefInput debrief) {
        return debriefService.createDebrief(debrief);
    }

    @MutationMapping
    public Debrief updateDebrief(@Argument("input") Map<String, Object> input) {
        return debriefService.updateDebrief(input);
    }

    @MutationMapping
    public Boolean deleteDebrief(@Argument("id") Long id) {
        return debriefService.deleteDebriefById(id);
    }
}