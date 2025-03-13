package org.example.debriefrepository.controller;

import lombok.RequiredArgsConstructor;
import org.example.debriefrepository.config.UserContext;
import org.example.debriefrepository.entity.Role;
import org.example.debriefrepository.service.RoleService;
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
    public Role roles(@Argument("input") Map<String, Object> input) {
        return roleService.getRole(input);
    }

    @MutationMapping
    public Role createRole(@Argument("input") RoleInput roleInput, @ContextValue String userId) {
        Role newRole = null;
        try {
            UserContext.setCurrentUserId(userId);
            newRole =  roleService.createUser(roleInput);
        }
        finally {
            UserContext.clear();
        }
        return newRole;
    }

    @MutationMapping
    public Role updateRole(@Argument("input") RoleInput role, @ContextValue String userId) {
        Role newRole = null;
        try {
            UserContext.setCurrentUserId(userId);
            newRole =  roleService.update(role);
        }
        finally {
            UserContext.clear();
        }
        return newRole;
    }

    @MutationMapping
    public Boolean deleteRole(@Argument String id) {
        return roleService.deleteById(id);
    }
}
