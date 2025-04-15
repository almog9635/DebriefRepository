package org.example.debriefrepository.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.debriefrepository.entity.Role;
import org.example.debriefrepository.repository.*;
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
public class RoleService {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final RoleRepository roleRepository;

    @Autowired
    private final GenericService<Role, RoleInput> genericService;

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
        List<Role> roles = new ArrayList<>();

        for (Map.Entry<String, Object> entry : chosenField.entrySet()) {
            String fieldName = entry.getKey();
            Object value = entry.getValue();

            if (value == null) {
                throw new IllegalArgumentException("The field '" + fieldName + "' is null.");
            }

            if (value instanceof Collection && ((Collection<?>) value).isEmpty()) {
                throw new IllegalArgumentException("The collection for field '" + fieldName + "' is empty.");
            }

            Object searchValue = (value instanceof Collection) ? ((Collection<?>) value).iterator().next() : value;

            try {
                List<Role> foundRoles = findRolesByField(fieldName, searchValue);
                roles.addAll(foundRoles);
            } catch (IllegalArgumentException e) {
                logger.error(e.getMessage());
                throw new RuntimeException("Failed to find users by field '" + fieldName + "'", e);
            }
        }

        return roles.stream().findFirst().orElseThrow(() -> new RuntimeException("Failed to find any roles by name."));
    }

    /***
     *
     * @param fieldName the name of the field i am trying to filter
     * @param value the value of that field
     * @return the list of the users
     */
    private List<Role> findRolesByField(String fieldName, Object value) {
        List<Role> roles = new ArrayList<>();

        for (Method method : userRepository.getClass().getMethods()) {
            if (!method.getName().startsWith("findBy") || method.getParameterCount() != 1) continue;

            try {
                String methodName = buildMethodName(fieldName, value);
                if (!method.getName().equals(methodName)) continue;

                if (value instanceof Map) {
                    value = ((Map<?, ?>) value).entrySet().iterator().next().getValue();
                }
                // Invoke the method dynamically
                Object result = method.invoke(roleRepository, value);

                if (result instanceof Optional<?>) {
                    ((Optional<?>) result).ifPresent(role -> roles.add((Role) role));
                } else if (result instanceof List<?>) {
                    roles.addAll((List<Role>) result);
                } else {
                    System.err.println("Unexpected return type: " + result.getClass().getName());
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException("Error invoking method: " + method.getName(), e);
            }
        }
        return roles;
    }

    /**
     * Converts nested field names into Spring Data JPA method format.
     * Example:
     * - "firstName" -> "findByFirstName"
     * - "group.id" -> "findByGroupId"
     */
    private String buildMethodName(String fieldName, Object value) {
        StringBuilder methodName = new StringBuilder("findBy");

        String[] parts = fieldName.split("\\.");
        for (String part : parts) {
            methodName.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1));
        }

        if (value instanceof Map<?, ?> mapValue) {
            if (!mapValue.isEmpty()) {
                String nestedKey = mapValue.keySet().iterator().next().toString();
                methodName.append(Character.toUpperCase(nestedKey.charAt(0))).append(nestedKey.substring(1));
            }
        }

        return methodName.toString();
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
        return genericService.setFields(role, input, null, skippedFields);
    }

}
