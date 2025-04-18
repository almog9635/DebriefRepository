package org.example.debriefrepository.controller;

import lombok.RequiredArgsConstructor;
import org.example.debriefrepository.config.UserContext;
import org.example.debriefrepository.entity.Group;
import org.example.debriefrepository.service.GroupService;
import org.example.debriefrepository.types.consts.consts;
import org.example.debriefrepository.types.input.GroupInput;
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
public class GroupController {

    @Autowired
    private final GroupService groupService;

    @QueryMapping
    public List<Group> getAllGroups() {
        return groupService.findAll();
    }

    @QueryMapping
    public Group groups(@Argument(consts.INPUT) Map<String, Object> input) {
        return groupService.getGroup(input);
    }

    @MutationMapping
    public Group createGroup(@Argument(consts.INPUT) GroupInput groupinput,  @ContextValue String userId) {
        Group newGroup = null;
        try{
            UserContext.setCurrentUserId(userId);
            newGroup = groupService.create(groupinput);
        }finally {
            UserContext.clear();
        }
        return newGroup;
    }

    @MutationMapping
    public Group updateGroup(@Argument(consts.INPUT) GroupInput input, @ContextValue String userId) {
        Group newGroup = null;
        try{
            UserContext.setCurrentUserId(userId);
            newGroup = groupService.update(input);
        }finally {
            UserContext.clear();
        }

        return newGroup;
    }

    @MutationMapping
    public Boolean deleteGroup(@Argument("id") String id) {
        return groupService.deleteById(id);
    }
}
