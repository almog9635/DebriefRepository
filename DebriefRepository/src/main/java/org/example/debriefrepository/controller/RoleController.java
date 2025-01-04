package org.example.debriefrepository.controller;

import lombok.RequiredArgsConstructor;
import org.example.debriefrepository.entity.Role;
import org.example.debriefrepository.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

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
    public Role roles(Long id) {
        return roleService.findById(id);
    }
}
