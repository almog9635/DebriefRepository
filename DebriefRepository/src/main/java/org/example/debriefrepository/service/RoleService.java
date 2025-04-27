package org.example.debriefrepository.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.debriefrepository.entity.Role;
import org.example.debriefrepository.repository.RoleRepository;
import org.example.debriefrepository.repository.UserRepository;
import org.example.debriefrepository.types.input.RoleInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

@Service
@RequiredArgsConstructor
public class RoleService extends GenericService<Role, RoleInput> {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final RoleRepository roleRepository;

    private static final Logger logger = LoggerFactory.getLogger(RoleService.class);

    public Role createRole(RoleInput roleInput) {
        Role role = new Role();
        try {
            return roleRepository.save(setFields(role, roleInput));
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        throw new RuntimeException("Error creating user");
    }

    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    /***
     *
     * @param chosenField is the chosen field i want to filter the users by
     * @return a list of users by the chosen field
     */
    public Role getRole(Map<String, Object> chosenField) {
        return super.getEntities(chosenField).getFirst();
    }

    @Transactional
    public Boolean deleteById(String id) {
        if (roleRepository.findById(id).isEmpty())
            return false;
        try {
            roleRepository.deleteById(id);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public Role update(RoleInput roleInput) {
        String roleId = roleInput.id();
        if (roleId == null || roleId.trim().isEmpty()) {
            throw new IllegalArgumentException("Role ID cannot be null or empty");
        }
        try {
            Role existingRole = roleRepository.findById(roleId)
                    .orElseThrow(() -> new IllegalArgumentException("Role not found with ID: " + roleId));
            return roleRepository.save(setFields(existingRole, roleInput));
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        throw new RuntimeException("Error modifying role");
    }

    private Role setFields(Role role, RoleInput input) {
        List<String> skippedFields = new ArrayList<>();
        skippedFields.add("id");
        return super.setFields(role, input, null, skippedFields);
    }

}
