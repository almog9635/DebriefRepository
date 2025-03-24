package org.example.debriefrepository.service;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.RequiredArgsConstructor;
import org.example.debriefrepository.entity.*;
import org.example.debriefrepository.repository.*;
import org.example.debriefrepository.types.content.ContentInput;
import org.example.debriefrepository.types.input.DebriefInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DebriefService {

    @Autowired
    private final DebriefRepository debriefRepository;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final GroupRepository groupRepository;

    @Autowired
    private final RoleRepository roleRepository;

    @Autowired
    private final MissionRepository missionRepository;

    @Autowired
    private final LessonRepository lessonRepository;

    @Autowired
    private final ContentService contentService;

    private static final Logger logger = LoggerFactory.getLogger(DebriefService.class);

    private Map<Class<? extends BaseEntity>, JpaRepository<? extends BaseEntity, String>> repositories;

    @PostConstruct
    private void init() {
        repositories = Map.of(
                User.class, userRepository,
                Group.class, groupRepository,
                Role.class, roleRepository,
                Mission.class, missionRepository,
                Lesson.class, lessonRepository,
                Debrief.class, debriefRepository
        );
    }

    public Debrief createDebrief(DebriefInput input) {
        Debrief debrief = new Debrief();
        try{
            List<String> skipedFields = new ArrayList<>();
            skipedFields.add("id");
            skipedFields.add("contentItems");
            skipedFields.add("lessons");
            skipedFields.add("missions");
            debrief = setFields(debrief, input, skipedFields);
            debriefRepository.save(debrief);
            setFields(debrief, input, null);
            return debriefRepository.save(debrief);
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        throw new RuntimeException("Error creating Debrief");
    }

    public List<Debrief> getAllDebriefs() {
        return debriefRepository.findAll();
    }

    public List<Debrief> getDebriefs(Map<String, Object> chosenField) {
        List<Debrief> debriefs = new ArrayList<>();

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
                List<Debrief> foundDebriefs = findDebriefsByField(fieldName, searchValue);
                debriefs.addAll(foundDebriefs);
            } catch (IllegalArgumentException e) {
                logger.error(e.getMessage());
                throw new RuntimeException("Failed to find users by field '" + fieldName + "'", e);
            }
        }

        return debriefs;
    }

    /***
     *
     * @param fieldName the name of the field i am trying to filter
     * @param value the value of that field
     * @return the list of the users
     */
    private List<Debrief> findDebriefsByField(String fieldName, Object value) {
        List<Debrief> debriefs = new ArrayList<>();

        for (Method method : debriefRepository.getClass().getMethods()) {
            if (!method.getName().startsWith("findBy") || method.getParameterCount() != 1) continue;

            try {
                String methodName = buildMethodName(fieldName, value);
                if (!method.getName().equals(methodName)) continue;

                if (value instanceof Map) {
                    value = ((Map<?, ?>) value).entrySet().iterator().next().getValue();
                }
                // Invoke the method dynamically
                Object result = method.invoke(debriefRepository, value);

                if (result instanceof Optional<?>) {
                    ((Optional<?>) result).ifPresent(user -> debriefs.add((Debrief) user));
                } else if (result instanceof List<?>) {
                    debriefs.addAll((List<Debrief>) result);
                } else {
                    System.err.println("Unexpected return type: " + result.getClass().getName());
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException("Error invoking method: " + method.getName(), e);
            }
        }
        return debriefs;
    }

    public Boolean deleteDebriefById(String id) {
        if(debriefRepository.existsById(id)) {
            try{
                debriefRepository.deleteById(id);
            }catch (Exception e){
                e.printStackTrace();
                return false;
            }
            return true;
        }

        return false;
    }

    public Debrief updateDebrief(DebriefInput debriefUpdate) {
        String debriefId = debriefUpdate.id();
        if (debriefId == null || debriefId.trim().isEmpty()) {
            throw new IllegalArgumentException("Debrief ID cannot be null or empty");
        }
        try {
            Debrief existingDebrief = debriefRepository.findById(debriefId)
                    .orElseThrow(() -> new IllegalArgumentException("Debrief not found with ID: " + debriefId));
            return debriefRepository.save(setFields(existingDebrief, debriefUpdate, null));
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        throw new RuntimeException("Error modifying user");
    }

    /*
        todo: define a const map of function of types that does not exist before the creation of the debrief
        such as contentItem or mission
     */
    private Debrief setFields(Debrief debrief,DebriefInput input, List<String> skipFields) {
        Map<String, Function<Object, Object>> customProcessors = new HashMap<>();
        customProcessors.put("contentItems", rawValue -> {
            if (rawValue instanceof ContentInput) {
                return contentService.createContent((ContentInput) rawValue, debrief.getId());
            }
            throw new IllegalArgumentException("Invalid value for contentItems field");
        });

        return setFieldsGeneric(debrief, input, customProcessors, skipFields);
    }

    private <T, U> T setFieldsGeneric(T entity, U input, Map<String, Function<Object,
            Object>> customProcessors, List<String> skipFields) {
        List<Field> fields = getAllFields(entity.getClass());
        for (Field entityField : fields) {
            entityField.setAccessible(true);
            String fieldName = entityField.getName();

            if ("metaData".equals(fieldName) || (Objects.nonNull(skipFields) &&
                    skipFields.contains(fieldName))) {
                continue;
            }
            try {

                if (customProcessors != null && customProcessors.containsKey(fieldName)) {
                    Object rawValue = getFieldValue(input, fieldName);
                    Object processedValue = customProcessors.get(fieldName).apply(rawValue);
                    entityField.set(entity, processedValue);
                    continue;
                }

                Object value = getFieldValue(input, fieldName);

                if (Objects.nonNull(value)) {
                    if (BaseEntity.class.isAssignableFrom(entityField.getType()) ||
                            BaseEntity.class.isAssignableFrom(findListType(entityField))) {
                        value = fetchEntities(entityField, value);

                    }
                    entityField.set(entity, value);
                } else {

                    Column annotation = entityField.getAnnotation(Column.class);
                    boolean isNullable = !(Objects.isNull(annotation)) &&  annotation.nullable();

                    if (!isNullable && Objects.nonNull(entity.getClass().getField(fieldName))) {
                        throw new IllegalArgumentException("Field '" + fieldName + "' cannot be null");
                    }
                }
            } catch (NoSuchFieldException e) {
                logger.warn("Field {} does not exist in {} input, skipping...", fieldName, input.getClass().getSimpleName());
            } catch (IllegalAccessException e) {
                logger.error("Unable to access field {} in {}", fieldName, entity.getClass().getSimpleName(), e);
                throw new RuntimeException("Failed to update field: " + fieldName, e);
            }
        }
        return entity;
    }

    private Object fetchEntities(Field field, Object value) {
        Class<?> type = findListType(field);
        if(type == UserRole.class){
            type = Role.class;
        }
        JpaRepository<? extends BaseEntity, String> repository = repositories.get(type);


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
