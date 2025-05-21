package org.example.debriefrepository.controller;

import graphql.schema.DataFetchingEnvironment;
import lombok.RequiredArgsConstructor;
import org.example.debriefrepository.config.UserContext.WithUserContext;
import org.example.debriefrepository.entity.Group;
import org.example.debriefrepository.service.GroupService;
import org.example.debriefrepository.types.consts.Const;
import org.example.debriefrepository.types.input.GroupInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class GroupController {

    @Autowired
    private final GroupService groupService;

    @QueryMapping
    public List<Group> getAllGroups() {
        return groupService.findAll();
    }

    @QueryMapping
    public Group groups(@Argument(Const.INPUT) Map<String, Object> input) {
        return groupService.getGroup(input);
    }

    @WithUserContext
    @MutationMapping
    public Group createGroup(@Argument(Const.INPUT) GroupInput input, DataFetchingEnvironment environment) {
        return groupService.create(input);
    }

    @WithUserContext
    @MutationMapping
    public Group updateGroup(@Argument(Const.INPUT) GroupInput input, DataFetchingEnvironment environment) {
        return groupService.update(input);
    }

    @MutationMapping
    public Boolean deleteGroup(@Argument("id") String id) {
        return groupService.deleteById(id);
    }
}
