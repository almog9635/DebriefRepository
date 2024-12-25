package org.example.debriefrepository.controller;

import lombok.RequiredArgsConstructor;
import org.example.debriefrepository.entity.Debrief;
import org.example.debriefrepository.service.DebriefService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class DebriefController {

    @Autowired
    private final DebriefService debriefService;

    @QueryMapping
    public Debrief debriefById(@Argument Long id) {
        return debriefService.getDebriefById(id);
    }

    @QueryMapping
    public List<Debrief> getAll() {
        return debriefService.getAllDebriefs();
    }

    @MutationMapping
    public Debrief create(@Argument Debrief debrief) {
        return debriefService.createDebrief(debrief);
    }

    @MutationMapping
    public Debrief update(@Argument Debrief debrief) {
        return debriefService.updateDebriefById(debrief.getId(), debrief);
    }

    @MutationMapping
    public Boolean delete(@Argument Long id) {
        return debriefService.deleteDebriefById(id);
    }
}