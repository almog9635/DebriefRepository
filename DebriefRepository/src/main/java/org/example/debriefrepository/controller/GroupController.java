package org.example.debriefrepository.controller;

import lombok.RequiredArgsConstructor;
import org.example.debriefrepository.entity.Group;
import org.example.debriefrepository.service.GroupService;
import org.example.debriefrepository.types.input.GroupInput;
import org.example.debriefrepository.types.update.GroupUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

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
    public Group groups(@Argument Long id) {
        return groupService.findById(id);
    }

    @QueryMapping
    public Group groupByName(String name) {
        return groupService.findByName(name);
    }

    @MutationMapping
    public Group addGroup(@Argument("input") GroupInput groupinput) {
        return groupService.create(groupinput);
    }

    @MutationMapping
    public Group updateGroup(@Argument("input") GroupUpdate groupUpdate) {
        return groupService.update(groupUpdate);
    }

    @MutationMapping
    public Boolean deleteGroup(@Argument("id") Long id) {
        return groupService.deleteById(id);
    }
}
