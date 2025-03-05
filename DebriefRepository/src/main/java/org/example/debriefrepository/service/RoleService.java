package org.example.debriefrepository.service;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.debriefrepository.entity.*;
import org.example.debriefrepository.repository.*;
import org.example.debriefrepository.types.RoleInput;
import org.example.debriefrepository.types.UserInput;
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
public class RoleService {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final GroupRepository groupRepository;

    @Autowired
    private final RoleRepository roleRepository;

    @Autowired
    private final MissionRepository missionRepository;

    @Autowired
    private final DebriefRepository debriefRepository;

    @Autowired
    private final LessonRepository lessonRepository;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private Map<Class<? extends BaseEntity>, JpaRepository<? extends BaseEntity, String>> REPOSITORY_MAP;

    @PostConstruct
    private void init() {
        REPOSITORY_MAP = Map.of(
                Group.class, groupRepository,
                Role.class, roleRepository,
                Mission.class, missionRepository,
                Lesson.class, lessonRepository,
                Debrief.class, debriefRepository
        );
    }

    public Role createUser(RoleInput roleInput) {
        Role role = new Role();
        try{
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

        return roles.stream().findFirst().orElseThrow(()-> new RuntimeException("Failed to find any roles by name."));
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

    public Role save(Role role) {
        return roleRepository.save(role);
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

    private List<Field> getAllFields(Class clazz){
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

    private Role setFields(Role role, Object input) {
        List<Field> entityFields = getAllFields(role.getClass());

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

                    }

                    entityField.set(role, value);
                }else{
                    Column annotation = entityField.getAnnotation(Column.class);
                    boolean isNullable = !(Objects.isNull(annotation)) &&  annotation.nullable();

                    if(!isNullable && !Objects.isNull(role.getClass().getField(fieldName))) {
                        throw new IllegalArgumentException("Field '" + fieldName + "' cannot be null");
                    }
                }
            } catch (NoSuchFieldException e) {
                logger.warn("Field {} does not exist in Role entity, skipping...", fieldName);
            } catch (IllegalAccessException e) {
                logger.error("Unable to access field {} in Role entity", fieldName, e);
                throw new RuntimeException("Failed to update role field: " + fieldName, e);
            }
        }

        return role;
    }

    /**
     * returns the value of the id from the database and checks if it exists
     */
    private Object fetchEntities(Field field, Object value) {
        Class<?> type = findListType(field);
        if(type == UserRole.class){
            type = Role.class;
        }
        JpaRepository<? extends BaseEntity, String> repository = REPOSITORY_MAP.get(type);


        // @ManyToOne fields cases
        if (field.isAnnotationPresent(ManyToOne.class) || value instanceof String) {
            try{
                return repository.findById((String)value)
                        .orElseThrow(() -> new IllegalArgumentException("Entity not found for ID: " + value));
            } catch (IllegalArgumentException e) {
                logger.error(e.getMessage());
                throw new RuntimeException("Failed to find entity by ID: " + value, e);
            }
        }

        // @OneToMany fields cases
        if(field.isAnnotationPresent(OneToMany.class) || value instanceof List<?>) {
            try{
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
}
