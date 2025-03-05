package org.example.debriefrepository.service;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.debriefrepository.entity.*;
import org.example.debriefrepository.repository.*;
import org.example.debriefrepository.types.GroupInput;
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
public class GroupService {

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

    @Transactional
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

    /**
     * Uses reflection to map input fields to the Group entity.
     */
    private Group setFields(Group group, Object input) {
        List<Field> entityFields = getAllFields(group.getClass());

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
                    entityField.set(group, value);
                }else{
                    Column annotation = entityField.getAnnotation(Column.class);
                    boolean isNullable = !(Objects.isNull(annotation)) &&  annotation.nullable();

                    if(!isNullable && !Objects.isNull(group.getClass().getField(fieldName))) {
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

        return group;
    }

    /**
     * Retrieves the value for a given field name from the input.
     */
    private Object getFieldValue(Object input, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        if (input instanceof Map) {
            return ((Map<String, Object>) input).get(fieldName);
        } else {
            Field inputField = input.getClass().getDeclaredField(fieldName);
            inputField.setAccessible(true);
            return inputField.get(input);
        }
    }

    /**
     * Recursively collects all declared fields for a class.
     */
    private List<Field> getAllFields(Class<?> clazz) {
        if (clazz == null) return Collections.emptyList();
        List<Field> fields = new ArrayList<>(getAllFields(clazz.getSuperclass()));
        fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
        return fields;
    }

    /**
     * Extracts the generic type argument for a List field.
     */
    private Class<?> findListType(Field field) {
        if (List.class.isAssignableFrom(field.getType())) {
            Type genericType = field.getGenericType();
            if (genericType instanceof ParameterizedType) {
                Type[] typeArgs = ((ParameterizedType) genericType).getActualTypeArguments();
                if (typeArgs.length == 1 && typeArgs[0] instanceof Class) {
                    return (Class<?>) typeArgs[0];
                }
            }
        }
        return field.getType();
    }

    /**
     * Fetches associated entities from the repository.
     */
    private Object fetchEntities(Field field, Object value) {
        Class<?> type = findListType(field);
        JpaRepository<? extends BaseEntity, String> repository;
        if (type.equals(User.class)) {
            repository = (JpaRepository<? extends BaseEntity, String>) userRepository;
        } else if (type.equals(Group.class)) {
            repository = groupRepository;
        } else {
            throw new IllegalArgumentException("Unsupported relation type: " + type.getName());
        }

        // Handle ManyToOne (or simple String ID) case
        if (field.isAnnotationPresent(ManyToOne.class) || value instanceof String) {
            try {
                return repository.findById(value.toString())
                        .orElseThrow(() -> new IllegalArgumentException("Entity not found for ID: " + value));
            } catch (IllegalArgumentException e) {
                logger.error(e.getMessage(), e);
                throw new RuntimeException("Failed to find entity by ID: " + value, e);
            }
        }
        // Handle OneToMany (or list of IDs)
        if (field.isAnnotationPresent(OneToMany.class) || value instanceof List<?>) {
            try {
                List<?> values = (List<?>) value;
                return values.stream()
                        .map(id -> repository.findById(id.toString())
                                .orElseThrow(() -> new IllegalArgumentException("Entity not found for ID: " + id)))
                        .collect(Collectors.toList());
            } catch (IllegalArgumentException e) {
                logger.error(e.getMessage(), e);
                throw new RuntimeException("Failed to find entities for value: " + value, e);
            }
        }
        throw new IllegalArgumentException("Unsupported field type or value for field: " + field.getName());
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
        try{
            groupRepository.deleteById(id);
        }catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
