package org.example.debriefrepository.service;

import jakarta.persistence.Column;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.debriefrepository.entity.Group;
import org.example.debriefrepository.entity.Role;
import org.example.debriefrepository.entity.User;
import org.example.debriefrepository.entity.UserRole;
import org.example.debriefrepository.repository.GroupRepository;
import org.example.debriefrepository.repository.RoleRepository;
import org.example.debriefrepository.repository.UserRepository;
import org.example.debriefrepository.types.UserInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static org.springframework.util.ReflectionUtils.findField;

@Service
@RequiredArgsConstructor
public class UserService {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final GroupRepository groupRepository;

    @Autowired
    private final RoleRepository roleRepository;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public User createUser(UserInput userInput) {
        for (Field field : userInput.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            if (field.getName().equals("id")) continue;
            try {
                Object value = field.get(userInput);

                if (value == null) {
                    throw new IllegalArgumentException("Field '" + field.getName() + "' cannot be null");
                }

                Field entityField = findField(User.class, field.getName());

                if (entityField == null) {
                    throw new IllegalArgumentException("Field '" + field.getName() + "' not found in User class");
                }

                entityField.setAccessible(true);
                Column annotation = entityField.getAnnotation(Column.class);
                boolean isNullable = annotation != null && annotation.nullable();

                if (!isNullable && value == null) {
                    throw new IllegalArgumentException("Field '" + field.getName() + "' cannot be null (non-nullable)");
                }

            } catch (IllegalArgumentException | IllegalAccessException e) {
                logger.error(e.getMessage());
                throw new IllegalStateException("Error accessing field: " + field.getName(), e);
            }
        }

        return userRepository.save(mapToUser(userInput));
    }


    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /***
     *
     * @param chosenField is the chosen field i want to filter the users by
     * @return a list of users by the chosen field
     */
    public List<User> getUser(Map<String, Object> chosenField) {
        List<User> users = new ArrayList<>();

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
                List<User> foundUsers = findUsersByField(fieldName, searchValue);
                users.addAll(foundUsers);
            } catch (IllegalArgumentException e) {
                logger.error(e.getMessage());
                throw new RuntimeException("Failed to find users by field '" + fieldName + "'", e);
            }
        }

        return users;
    }

    /***
     *
     * @param fieldName the name of the field i am trying to filter
     * @param value the value of that field
     * @return the list of the users
     */
    private List<User> findUsersByField(String fieldName, Object value) {
        List<User> users = new ArrayList<>();

        for (Method method : userRepository.getClass().getMethods()) {
            if (!method.getName().startsWith("findBy") || method.getParameterCount() != 1) continue;

            try {
                String methodName = buildMethodName(fieldName, value);
                if (!method.getName().equals(methodName)) continue;

                if (value instanceof Map) {
                    value = ((Map<?, ?>) value).entrySet().iterator().next().getValue();
                }
                // Invoke the method dynamically
                Object result = method.invoke(userRepository, value);

                if (result instanceof Optional<?>) {
                    ((Optional<?>) result).ifPresent(user -> users.add((User) user));
                } else if (result instanceof List<?>) {
                    users.addAll((List<User>) result);
                } else {
                    System.err.println("Unexpected return type: " + result.getClass().getName());
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException("Error invoking method: " + method.getName(), e);
            }
        }
        return users;
    }

    @Transactional
    public Boolean deleteById(String id) {
        if (userRepository.findById(id).isEmpty())
            return false;
        try{
            userRepository.deleteById(id);
        }catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /* todo: fix graphql update mutation, */
    public User update(Map<String, Object> userInput) {
        String userId = (String) userInput.get("id");
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }

        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        System.out.println(userInput.size());

        for (Map.Entry<String, Object> entry : userInput.entrySet()) {
            if (!entry.getKey().equals("id")) { // Prevent updating ID
                try {
                    Field field = User.class.getDeclaredField(entry.getKey());
                    if (Objects.isNull(field)) {
                        throw new IllegalArgumentException("The field " + entry.getKey() + " is null or empty");
                    }
                    field.setAccessible(true);

                    Column annotation = field.getAnnotation(Column.class);
                    boolean isNullable = annotation != null && annotation.nullable();

                    if (!isNullable && Objects.isNull(entry.getValue())) {
                        throw new IllegalArgumentException("The field " + entry.getKey() + " cannot be null");
                    }

                    field.set(existingUser, entry.getValue());
                } catch (NoSuchFieldException e) {
                    logger.warn("Field {} does not exist in User entity, skipping...", entry.getKey());
                } catch (IllegalAccessException e) {
                    logger.error("Unable to access field {} in User entity", entry.getKey(), e);
                    throw new RuntimeException("Failed to update user field: " + entry.getKey(), e);
                }
            }
        }

        return userRepository.save(existingUser);
    }

    private User mapToUser(UserInput input) {
        User user = new User();
        user.setFirstName(input.firstName());
        user.setLastName(input.lastName());
        user.setPassword(input.password());
        user.setServiceType(input.serviceType());
        user.setRank(input.rank());
        List<UserRole> userRoles = input.roles().stream()
                .map(roleInput -> {
                    Role role = roleRepository.findByName(roleInput.name());
                    UserRole userRole = new UserRole();
                    userRole.setUser(user);
                    userRole.setRole(role);

                    return userRole;
                })
                .toList();
        Group group = groupRepository.findByName(input.group());
        user.setGroup(group);
        user.setRoles(userRoles);
        return user;
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
