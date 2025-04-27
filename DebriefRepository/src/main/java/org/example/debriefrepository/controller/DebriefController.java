package org.example.debriefrepository.controller;

import graphql.schema.DataFetchingEnvironment;
import lombok.RequiredArgsConstructor;
import org.example.debriefrepository.config.UserContext.WithUserContext;
import org.example.debriefrepository.entity.Debrief;
import org.example.debriefrepository.service.debrief.DebriefService;
import org.example.debriefrepository.types.consts.Const;
import org.example.debriefrepository.types.input.DebriefInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.ContextValue;
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
    public List<Debrief> debriefs(@Argument(Const.INPUT) Map<String, Object> input) {
        return debriefService.getDebriefs(input);
    }

    @QueryMapping
    public List<Debrief> getAllDebriefs() {
        return debriefService.getAllDebriefs();
    }

    @WithUserContext
    @MutationMapping
    public Debrief createDebrief(@Argument(Const.INPUT) DebriefInput input, DataFetchingEnvironment environment) {
        return debriefService.createDebrief(input);
    }

    @MutationMapping
    public Debrief updateDebrief(@Argument(Const.INPUT) DebriefInput input, DataFetchingEnvironment environment) {
        return debriefService.updateDebrief(input);

    }

    @MutationMapping
    public Boolean deleteDebrief(@Argument("id") String id) {
        return debriefService.deleteDebriefById(id);
    }
}