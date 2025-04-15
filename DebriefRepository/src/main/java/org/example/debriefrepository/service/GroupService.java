package org.example.debriefrepository.service;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.debriefrepository.entity.*;
import org.example.debriefrepository.repository.*;
import org.example.debriefrepository.types.input.GroupInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

@Service
@RequiredArgsConstructor
public class GroupService {

    @Autowired
    private final GroupRepository groupRepository;

    @Autowired
    private final GenericService<Group, GroupInput> genericService;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public List<Group> findAll() {
        return groupRepository.findAll();
    }

    /**
     * Retrieves a group by filtering on a chosen field.
     * Iterates over the provided map, dynamically builds a repository method name and invokes it.
     */
    public Group getGroup(Map<String, Object> chosenField) {
        Group group = null;
        for (Map.Entry<String, Object> entry : chosenField.entrySet()) {
            String fieldName = entry.getKey();
            Object value = entry.getValue();

            if (value == null) {
                throw new IllegalArgumentException("The field '" + fieldName + "' is null.");
            }
            if (value instanceof Collection && ((Collection<?>) value).isEmpty()) {
                throw new IllegalArgumentException("The collection for field '" + fieldName + "' is empty.");
            }
            Object searchValue = (value instanceof Collection)
                    ? ((Collection<?>) value).iterator().next()
                    : value;
            try {
                group = findGroupsByField(fieldName, searchValue);
                if (group == null) {
                    throw new IllegalArgumentException("The field '" + fieldName + "' is not found.");
                }
            } catch (IllegalArgumentException e) {
                logger.error("Error finding groups by field '{}' with value '{}': {}",
                        fieldName, searchValue, e.getMessage(), e);
                throw new RuntimeException("Failed to find groups by field '" + fieldName + "'", e);
            }
        }
        return group;
    }

    /**
     * Dynamically invokes repository methods (e.g., findByName) to find a group.
     */
    private Group findGroupsByField(String fieldName, Object value) {
        List<Group> groups = new ArrayList<>();
        for (Method method : groupRepository.getClass().getMethods()) {
            if (!method.getName().startsWith("findBy") || method.getParameterCount() != 1) continue;
            try {
                String methodName = buildMethodName(fieldName, value);
                if (!method.getName().equals(methodName)) continue;

                // In case the value is nested in a Map
                if (value instanceof Map) {
                    value = ((Map<?, ?>) value).entrySet().iterator().next().getValue();
                }
                Object result = method.invoke(groupRepository, value);
                if (result instanceof Optional<?>) {
                    ((Optional<?>) result).ifPresent(g -> groups.add((Group) g));
                } else if (result instanceof List<?>) {
                    groups.addAll((List<Group>) result);
                } else if (result instanceof Group) {
                    groups.add((Group) result);
                } else {
                    logger.error("Unexpected return type: " + result.getClass().getName());
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                logger.error("Error invoking method: " + method.getName(), e);
                throw new RuntimeException("Error invoking method: " + method.getName(), e);
            }
        }
        return groups.stream().findFirst().orElse(null);
    }

    public Group update(GroupInput input) {
        try {
            String groupId = input.id();
            Group existingGroup = groupRepository.findById(groupId)
                    .orElseThrow(() -> new IllegalArgumentException("Group not found with ID: " + groupId));
            return groupRepository.save(setFields(existingGroup, input));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException("Error updating group", e);
        }
    }

    public Group create(GroupInput groupInput) {
        return groupRepository.save(setFields(new Group(), groupInput));
    }

    private Group setFields(Group group, GroupInput input) {
        List<String> skippedFields = new ArrayList<>();
        skippedFields.add("id");
        return genericService.setFields(group, input, null, skippedFields);
    }

    /**
     * Builds a repository method name from a field name.
     */
    private String buildMethodName(String fieldName, Object value) {
        StringBuilder methodName = new StringBuilder("findBy");
        String[] parts = fieldName.split("\\.");
        for (String part : parts) {
            methodName.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1));
        }
        if (value instanceof Map<?, ?> mapValue && !mapValue.isEmpty()) {
            String nestedKey = mapValue.keySet().iterator().next().toString();
            methodName.append(Character.toUpperCase(nestedKey.charAt(0))).append(nestedKey.substring(1));
        }

        return methodName.toString();
    }

    @Transactional
    public Boolean deleteById(String id) {
        if (groupRepository.findById(id).isEmpty())
            return false;
        try {
            groupRepository.deleteById(id);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
