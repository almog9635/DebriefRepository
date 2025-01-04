package org.example.debriefrepository.controller;

import lombok.RequiredArgsConstructor;
import org.example.debriefrepository.entity.Debrief;
import org.example.debriefrepository.types.input.DebriefInput;
import org.example.debriefrepository.service.DebriefService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class DebriefController {

    @Autowired
    private final DebriefService debriefService;

    @QueryMapping
    public Debrief debriefs(@Argument Long id) {
        return debriefService.getDebriefById(id);
    }

    @QueryMapping
    public List<Debrief> getAllDebriefs() {
        return debriefService.getAllDebriefs();
    }

    @MutationMapping
    public Debrief addDebrief(@Argument DebriefInput debrief) {
        return debriefService.createDebrief(new Debrief());
    }

    @MutationMapping
    public Debrief updateDebrief(@Argument Long id, @Argument DebriefInput debrief) {
        return debriefService.updateDebriefById(id, new Debrief());
    }

    @MutationMapping
    public Boolean deleteDebrief(@Argument Long id) {
        return debriefService.deleteDebriefById(id);
    }
}