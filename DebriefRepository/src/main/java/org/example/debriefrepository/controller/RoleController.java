package org.example.debriefrepository.controller;

import graphql.schema.DataFetchingEnvironment;
import lombok.RequiredArgsConstructor;
import org.example.debriefrepository.config.UserContext.WithUserContext;
import org.example.debriefrepository.entity.Role;
import org.example.debriefrepository.service.RoleService;
import org.example.debriefrepository.types.consts.Const;
import org.example.debriefrepository.types.input.RoleInput;
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
public class RoleController {

    @Autowired
    private final RoleService roleService;

    @QueryMapping
    public List<Role> getAllRoles() {
        return roleService.findAll();
    }

    @QueryMapping
    public Role roles(@Argument(Const.INPUT) Map<String, Object> input) {
        return roleService.getRole(input);
    }

    @WithUserContext
    @MutationMapping
    public Role createRole(@Argument(Const.INPUT) RoleInput roleInput, DataFetchingEnvironment environment) {
        return roleService.createRole(roleInput);
    }

    @WithUserContext
    @MutationMapping
    public Role updateRole(@Argument(Const.INPUT) RoleInput roleInput, DataFetchingEnvironment environment) {
        return roleService.update(roleInput);
    }

    @MutationMapping
    public Boolean deleteRole(@Argument String id) {
        return roleService.deleteById(id);
    }
}
