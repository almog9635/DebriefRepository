package org.example.debriefrepository.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.debriefrepository.entity.Role;
import org.example.debriefrepository.entity.User;
import org.example.debriefrepository.entity.UserRole;
import org.example.debriefrepository.repository.RoleRepository;
import org.example.debriefrepository.repository.UserRepository;
import org.example.debriefrepository.types.input.UserInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final RoleRepository roleRepository;

    @Autowired
    private final GenericService<User, UserInput> genericService;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public User createUser(UserInput userInput) {
        User user = new User();
        List<String> skippedFields = new ArrayList<>();
        skippedFields.add("id");
        try {
            return userRepository.save(setFields(user, userInput, skippedFields));
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        throw new RuntimeException("Error creating user");
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
        try {
            userRepository.deleteById(id);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public User update(UserInput userInput) {
        String userId = userInput.id();
        List<String> skippedFields = new ArrayList<>();
        skippedFields.add("id");
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }
        try {
            User existingUser = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
            return userRepository.save(setFields(existingUser, userInput, skippedFields));
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        throw new RuntimeException("Error modifying user");
    }

    private User setFields(User user, UserInput input, List<String> skippedFields) {
        Map<String, Function<Object, Object>> customProcessors = new HashMap<>();
        customProcessors.put("roles", value -> {
            List<Role> roles;
            try {
                List<?> values = (List<?>) value;
                roles = values.stream()
                        .map(id -> roleRepository.findById(id.toString())
                                .orElseThrow(() -> new IllegalArgumentException("Role not found with ID: " + id.toString())))
                        .toList();

                user.getRoles().clear();
                List<UserRole> newRoles = roles.stream()
                        .map(role -> {
                            UserRole userRole = new UserRole();
                            userRole.setRole(role);
                            userRole.setUser(user);
                            return userRole;
                        })
                        .collect(Collectors.toCollection(ArrayList::new));
                return user.getRoles().addAll(newRoles);
            } catch (IllegalArgumentException e) {
                logger.error(e.getMessage());
                throw new RuntimeException(e.getMessage());
            }
        });
        return genericService.setFields(user, input, customProcessors, skippedFields);
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
