package org.example.debriefrepository.controller;

import lombok.RequiredArgsConstructor;
import org.example.debriefrepository.config.UserContext;
import org.example.debriefrepository.entity.Debrief;
import org.example.debriefrepository.service.debrief.DebriefService;
import org.example.debriefrepository.types.consts.consts;
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
    public List<Debrief> debriefs(@Argument(consts.INPUT) Map<String, Object> input) {
        return debriefService.getDebriefs(input);
    }

    @QueryMapping
    public List<Debrief> getAllDebriefs() {
        return debriefService.getAllDebriefs();
    }

    @MutationMapping
    public Debrief createDebrief(@Argument(consts.INPUT) DebriefInput debrief, @ContextValue String userId) {
        Debrief newDebrief = null;
        try{
            UserContext.setCurrentUserId(userId);
            newDebrief =  debriefService.createDebrief(debrief);
        } finally {
            UserContext.clear();
        }
        return newDebrief;
    }

    @MutationMapping
    public Debrief updateDebrief(@Argument(consts.INPUT) DebriefInput input, @ContextValue String userId) {
        Debrief updatedDebrief = null;
        try{
            UserContext.setCurrentUserId(userId);
            updatedDebrief =  debriefService.updateDebrief(input);
        } finally {
            UserContext.clear();
        }
        return updatedDebrief;
    }

    @MutationMapping
    public Boolean deleteDebrief(@Argument("id") String id) {
        return debriefService.deleteDebriefById(id);
    }
}