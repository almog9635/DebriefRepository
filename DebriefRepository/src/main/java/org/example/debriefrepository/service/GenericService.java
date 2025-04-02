package org.example.debriefrepository.service;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.RequiredArgsConstructor;
import org.example.debriefrepository.entity.*;
import org.example.debriefrepository.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GenericService <T extends BaseEntity, U>{

    private final DebriefRepository debriefRepository;
    
    private final UserRepository userRepository;
    
    private final GroupRepository groupRepository;
    
    private final RoleRepository roleRepository;
    
    private final TaskRepository taskRepository;
    
    private final LessonRepository lessonRepository;

    private final CommentRepository commentRepository;

    private final ParagraphRepository paragraphRepository;

    private final TableRepository tableRepository;

    private final RowRepository rowRepository;

    private final TableColumnRepository tableColumnRepository;

    private final CellRepository cellRepository;

    private final Logger logger = LoggerFactory.getLogger(GenericService.class);

    private Map<Class<? extends BaseEntity>, JpaRepository<? extends BaseEntity, String>> repositories;

    @PostConstruct
    private void init() {
        repositories = Map.ofEntries(
                Map.entry(User.class, userRepository),
                Map.entry(Group.class, groupRepository),
                Map.entry(Role.class, roleRepository),
                Map.entry(Task.class, taskRepository),
                Map.entry(Lesson.class, lessonRepository),
                Map.entry(Debrief.class, debriefRepository),
                Map.entry(Comment.class, commentRepository),
                Map.entry(Paragraph.class, paragraphRepository),
                Map.entry(Table.class, tableRepository),
                Map.entry(Row.class, rowRepository),
                Map.entry(TableColumn.class, tableColumnRepository),
                Map.entry(Cell.class, cellRepository)
        );
    }
    
    public T setFieldsGeneric(T entity, U input, Map<String, Function<Object,
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

                Object value = getFieldValue(input, fieldName);

                if (customProcessors != null && customProcessors.containsKey(fieldName)) {
                    Object processedValue = customProcessors.get(fieldName).apply(value);
                    entityField.set(entity, processedValue);
                    continue;
                }

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

    /**
     * Recursively collects all declared fields for a class.
     */
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
     * Retrieves the value for a given field name from the input.
     */
    private Object getFieldValue(Object input, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        if (input instanceof Map) {
            return ((Map<String, Object>) input).get(fieldName); // Get value from Map
        } else {
            Class<?> clazz = input.getClass();

            while (clazz != null && clazz != Object.class) {
                try {
                    Field field = clazz.getDeclaredField(fieldName);
                    field.setAccessible(true); // Access private field
                    return field.get(input);  // Return the value
                } catch (NoSuchFieldException e) {
                    clazz = clazz.getSuperclass(); // Move to superclass
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Cannot access field: " + fieldName, e);
                }
            }
        }
        return null;
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
