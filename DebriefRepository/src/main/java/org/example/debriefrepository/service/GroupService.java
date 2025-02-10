package org.example.debriefrepository.service;

import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.debriefrepository.entity.Group;
import org.example.debriefrepository.entity.User;
import org.example.debriefrepository.repository.GroupRepository;
import org.example.debriefrepository.repository.UserRepository;
import org.example.debriefrepository.types.GroupInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static org.springframework.util.ReflectionUtils.findField;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;

    private final UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public List<Group> findAll() {
        return groupRepository.findAll();
    }

    /***
     *
     * @param chosenField is the chosen field i want to filter the groups by
     * @return a list of groups by the chosen field
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

            Object searchValue = (value instanceof Collection) ? ((Collection<?>) value).iterator().next() : value;

            try {
                group = findGroupsByField(fieldName, searchValue);
                if (group == null) {
                    throw new IllegalArgumentException("The field '" + fieldName + "' is not found.");
                }
            } catch (IllegalArgumentException e) {
                System.err.println("Error finding groups by field '" + fieldName + "' with value '" + searchValue + "': " + e.getMessage());
                throw new RuntimeException("Failed to find groups by field '" + fieldName + "'", e);
            }
        }

        return group;
    }

    /***
     *
     * @param fieldName the name of the field i am trying to filter
     * @param value the value of that field
     * @return the list of the groups
     */
    private Group findGroupsByField(String fieldName, Object value) {
        List<Group> groups = new ArrayList<>();

        for (Method method : groupRepository.getClass().getMethods()) {
            if (!method.getName().startsWith("findBy") || method.getParameterCount() != 1) continue;

            try {
                String methodName = buildMethodName(fieldName, value);
                if (!method.getName().equals(methodName)) continue;

                if (value instanceof Map) {
                    value = ((Map<?, ?>) value).entrySet().iterator().next().getValue();
                }
                // Invoke the method dynamically
                Object result = method.invoke(groupRepository, value);

                if (result instanceof Optional<?>) {
                    ((Optional<?>) result).ifPresent(group -> groups.add((Group) group));
                } else if (result instanceof List<?>) {
                    groups.addAll((List<Group>) result);
                }
                if(result instanceof Group) {
                    groups.add((Group) result);
                }else {
                    System.err.println("Unexpected return type: " + result.getClass().getName());
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                logger.error("Error finding groups by field '" + fieldName + "' with value '" + value + "'", e);
                throw new RuntimeException("Error invoking method: " + method.getName(), e);
            }
        }
        return groups.stream().findFirst().orElse(null);
    }

    public Group update(Map<String, Object> input) {
        try {
            Group existingGroup = groupRepository.findById((Long) input.get("id"))
                    .orElse(null);
            if (existingGroup != null) {
                for (Map.Entry<String, Object> entry : input.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    switch (key) {
                        case "name":
                            existingGroup.setName((String) value);
                            break;

                        case "commander":
                            existingGroup.setCommander((userRepository.findById(Long.parseLong(value.toString()))
                                    .orElse(null)));
                            break;

                        case "users":

                            break;

                        default:
                            throw new IllegalArgumentException("Unsupported key: " + key);
                    }
                }
                return existingGroup;
            }
            return existingGroup;
        } catch (Error error) {
            error.printStackTrace();
            throw new RuntimeException("Unexpected error occurred");
        }

    }

    public Group create(GroupInput groupInput) {
        for (Field field : groupInput.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            if (field.getName().equals("id")) continue;
            try {
                Object value = field.get(groupInput);
                Field entityField = findField(Group.class, field.getName());

                if (entityField == null && value == null) {
                    throw new IllegalArgumentException("Field '" + field.getName() + "' not found in User class");
                }

                entityField.setAccessible(true);
                boolean isNullable = isIsNullable(field, entityField, value);

                if (!isNullable && value == null) {
                    throw new IllegalArgumentException("Field '" + field.getName() + "' cannot be null (non-nullable)");
                }

            } catch (IllegalAccessException e) {
                logger.error(e.getMessage(), e);
                throw new IllegalStateException("Error accessing field: " + field.getName(), e);
            }
        }

        return groupRepository.save(mapToGroup(groupInput));
    }

    /***
     *
     * @param field the raw field
     * @param entityField the entity field
     * @param value the value of the raw field
     * @return if the annotation from the database can be null or not
     */
    private static boolean isIsNullable(Field field, Field entityField, Object value) {
        Column annotation = entityField.getAnnotation(Column.class);
        boolean isNullable = true;

        // If @Column exists, check nullable property
        if (annotation != null) {
            isNullable = annotation.nullable();
        }

        // If the field is an association (like @ManyToOne), assume it's nullable
        if (entityField.getAnnotation(OneToOne.class) != null ||
                entityField.getAnnotation(ManyToOne.class) != null) {
            isNullable = true; // Assume nullable for relations
        }

        // check if the value is null when it shouldn't be
        if (!isNullable && value == null) {
            throw new IllegalArgumentException("Field '" + field.getName() + "' cannot be null (non-nullable)");
        }
        return isNullable;
    }

    @Transactional
    public Boolean deleteById(Long id) {
        if (groupRepository.existsById(id)) {
            try{
                groupRepository.deleteById(id);
            }catch (Exception e) {
                logger.error(e.getMessage(), e);
                return false;
            }
            return true;
        }
        return false;
    }

    private Group mapToGroup(GroupInput groupInput) {
        Group group = new Group();
        group.setName(groupInput.name());
        group.setCommander(null);
        try {
            if (groupInput.commander() != null) {
                User commander = (userRepository.findById(groupInput.commander().toString())
                        .orElse(null));
                if (commander == null) {
                    throw new IllegalArgumentException("The commander id " + groupInput.commander() + " is not found");
                }
                group.setCommander(commander);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Unexpected error occurred");
        }

        return group;
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

}
