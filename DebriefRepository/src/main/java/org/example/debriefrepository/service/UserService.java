package org.example.debriefrepository.service;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.debriefrepository.entity.*;
import org.example.debriefrepository.repository.*;
import org.example.debriefrepository.types.input.UserInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final GroupRepository groupRepository;

    @Autowired
    private final RoleRepository roleRepository;

    @Autowired
    private final TaskRepository taskRepository;

    @Autowired
    private final DebriefRepository debriefRepository;

    @Autowired
    private final LessonRepository lessonRepository;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private Map<Class<? extends BaseEntity>, JpaRepository<? extends BaseEntity, String>> repositories;

    @PostConstruct
    private void init() {
        repositories = Map.of(
                Group.class, groupRepository,
                Role.class, roleRepository,
                Task.class, taskRepository,
                Lesson.class, lessonRepository,
                Debrief.class, debriefRepository
        );
    }

    public User createUser(UserInput userInput) {
        User user = new User();
        try {
            return userRepository.save(setFields(user, userInput));
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
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }
        try {
            User existingUser = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
            return userRepository.save(setFields(existingUser, userInput));
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        throw new RuntimeException("Error modifying user");
    }

    private User setFields(User user, Object input) {
        List<Field> entityFields = getAllFields(user.getClass());

        for (Field entityField : entityFields) {
            entityField.setAccessible(true);
            String fieldName = entityField.getName();

            if ("id".equals(fieldName) || "metaData".equals(fieldName)) continue;

            try {
                Object value = getFieldValue(input, fieldName);

                if (value != null) {
                    if (BaseEntity.class.isAssignableFrom(entityField.getType()) ||
                            BaseEntity.class.isAssignableFrom(findListType(entityField))) {
                        value = fetchEntities(entityField, value);

                        /* ask chanan if there is a better way */
                        if (entityField.getName().equals("roles")) {
                            user.getRoles().clear();
                            List<UserRole> newRoles = ((List<Role>) value).stream()
                                    .map(role -> {
                                        UserRole userRole = new UserRole();
                                        userRole.setRole(role);
                                        userRole.setUser(user);

                                        return userRole;
                                    })
                                    .collect(Collectors.toCollection(ArrayList::new));
                            user.getRoles().addAll(newRoles);
                            continue;
                        }
                    }

                    entityField.set(user, value);
                } else {
                    Column annotation = entityField.getAnnotation(Column.class);
                    boolean isNullable = !(Objects.isNull(annotation)) && annotation.nullable();

                    if (!isNullable && !Objects.isNull(user.getClass().getField(fieldName))) {
                        throw new IllegalArgumentException("Field '" + fieldName + "' cannot be null");
                    }
                }
            } catch (NoSuchFieldException e) {
                logger.warn("Field {} does not exist in User entity, skipping...", fieldName);
            } catch (IllegalAccessException e) {
                logger.error("Unable to access field {} in User entity", fieldName, e);
                throw new RuntimeException("Failed to update user field: " + fieldName, e);
            }
        }

        return user;
    }

    /**
     * returns the value of the id from the database and checks if it exists
     */
    private Object fetchEntities(Field field, Object value) {
        Class<?> type = findListType(field);
        if (type == UserRole.class) {
            type = Role.class;
        }
        JpaRepository<? extends BaseEntity, String> repository = repositories.get(type);


        // @ManyToOne fields cases
        if (field.isAnnotationPresent(ManyToOne.class) || value instanceof String) {
            try {
                return repository.findById((String) value)
                        .orElseThrow(() -> new IllegalArgumentException("Entity not found for ID: " + value));
            } catch (IllegalArgumentException e) {
                logger.error(e.getMessage());
                throw new RuntimeException("Failed to find entity by ID: " + value, e);
            }
        }

        // @OneToMany fields cases
        if (field.isAnnotationPresent(OneToMany.class) || value instanceof List<?>) {
            try {
                List<?> values = (List<?>) value;
                return values.stream()
                        .map(id -> repository.findById(id.toString())
                                .orElseThrow(() -> new IllegalArgumentException("Entity not found for ID: " + id)))
                        .toList();
            } catch (IllegalArgumentException e) {
                logger.error(e.getMessage());
                throw new RuntimeException("Failed to find entity by ID: " + value, e);
            }
        }

        throw new IllegalArgumentException("Unsupported field type or value for field: " + field.getName());

    }

    private Object getFieldValue(Object input, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        if (input instanceof Map) {
            return ((Map<String, Object>) input).get(fieldName); // Get value from Map
        } else {
            Field inputField = input.getClass().getDeclaredField(fieldName);
            inputField.setAccessible(true);
            return inputField.get(input); // Get value via reflection
        }
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

    private List<Field> getAllFields(Class clazz) {
        if (clazz == null) {
            return Collections.emptyList();
        }

        List<Field> result = new ArrayList<>(getAllFields(clazz.getSuperclass()));
        List<Field> filteredFields = Arrays.stream(clazz.getDeclaredFields())
                .collect(Collectors.toList());
        result.addAll(filteredFields);
        return result;
    }

    /**
     * finds the type that list is made of
     */
    private Class<?> findListType(Field field) {
        if (List.class.isAssignableFrom(field.getType())) {
            Type genericType = field.getGenericType();

            // Ensure it's a ParameterizedType (i.e., List<T>)
            if (genericType instanceof ParameterizedType) {
                Type[] actualTypeArguments = ((ParameterizedType) genericType).getActualTypeArguments();

                // Get the first generic type argument (T in List<T>)
                if (actualTypeArguments.length == 1 && actualTypeArguments[0] instanceof Class) {
                    return (Class<?>) actualTypeArguments[0];
                }
            }
        }
        return field.getType();
    }

}
