package org.example.debriefrepository.service;

import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.debriefrepository.entity.BaseEntity;
import org.example.debriefrepository.entity.Role;
import org.example.debriefrepository.entity.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ResolvableType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@NoArgsConstructor
public abstract class GenericService<T extends BaseEntity, U> {

    @Setter(onMethod = @__({@Autowired}))
    private List<JpaRepository<? extends BaseEntity, String>> jpaRepositoryList;

    private final Class<T> domainClass = (Class<T>)
            ((ParameterizedType)getClass().getGenericSuperclass())
                    .getActualTypeArguments()[0];

    private final Logger logger = LoggerFactory.getLogger(GenericService.class);

    public List<T> getEntities(Map<String, Object> chosenField) {
        List<T> entities = new ArrayList<>();

        for (Map.Entry<String, Object> entry : chosenField.entrySet()) {
            String fieldName = entry.getKey();
            Object value = entry.getValue();

            if (Objects.isNull(value)) {
                throw new IllegalArgumentException("The field '" + fieldName + "' is null.");
            }

            if (value instanceof Collection && ((Collection<?>) value).isEmpty()) {
                throw new IllegalArgumentException("The collection for field '" + fieldName + "' is empty.");
            }

            Object searchValue = (value instanceof Collection) ? ((Collection<?>) value).iterator().next() : value;

            try {
                List<T> foundEntities = findEntitiesByField(fieldName, searchValue);
                entities.addAll(foundEntities);
            } catch (IllegalArgumentException e) {
                logger.error(e.getMessage());
                throw new RuntimeException("Failed to find users by field '" + fieldName + "'", e);
            }
        }

        return entities;
    }

    /***
     *
     * @param fieldName the name of the field i am trying to filter
     * @param value the value of that field
     * @return the list of the users
     */
    private List<T> findEntitiesByField(String fieldName, Object value) {
        List<T> entities = new ArrayList<>();

        JpaRepository<? extends BaseEntity, String> repository = getRepositoryFor(domainClass);

        for (Method method : repository.getClass().getMethods()) {
            if (!method.getName().startsWith("findBy") || method.getParameterCount() != 1) continue;

            try {
                String methodName = buildMethodName(fieldName, value);
                if (!method.getName().equals(methodName)) continue;

                if (value instanceof Map) {
                    value = ((Map<?, ?>) value).entrySet().iterator().next().getValue();
                }

                Object result = method.invoke(repository, value);

                if (result instanceof Optional<?>) {
                    ((Optional<?>) result).ifPresent(entity -> entities.add((T) entity));
                } else if (result instanceof List<?>) {
                    entities.addAll((List<T>) result);
                } else {
                    System.err.println("Unexpected return type: " + result.getClass().getName());
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException("Error invoking method: " + method.getName(), e);
            }
        }
        return entities;
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


    public T setFields(T entity, U input, Map<String, Function<Object, Object>> customProcessors,
                       List<String> skipFields) {
        List<Field> fields = getAllFields(entity.getClass());
        for (Field entityField : fields) {
            entityField.setAccessible(true);
            String fieldName = entityField.getName();
            boolean processed = false;

            if ("metaData".equals(fieldName) || (Objects.nonNull(skipFields) &&
                    skipFields.contains(fieldName))) {
                processed = true;
            }

            try {
                Object value = getFieldValue(input, fieldName);

                if (!processed && Objects.nonNull(customProcessors) && customProcessors.containsKey(fieldName)
                        && Objects.nonNull(value)) {
                    Object processedValue = customProcessors.get(fieldName).apply(value);

                    if(!entityField.getName().equals("roles")) {
                        entityField.set(entity, processedValue);
                    }

                    processed = true;
                }

                if (Objects.nonNull(value) && !processed) {
                    if (BaseEntity.class.isAssignableFrom(entityField.getType()) ||
                            BaseEntity.class.isAssignableFrom(findListType(entityField))) {
                        value = fetchEntities(entityField, value);

                    }
                    entityField.set(entity, value);
                } else if (Objects.isNull(value) && !processed) {

                    Column annotation = entityField.getAnnotation(Column.class);
                    boolean isNullable = !(Objects.isNull(annotation)) && annotation.nullable();

                    if (!isNullable && Objects.nonNull(entity.getClass().getField(fieldName)) && !processed) {
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

        if (type == UserRole.class) {
            type = Role.class;
        }

        JpaRepository<? extends BaseEntity, String> repository = getRepositoryFor(type);

        if (field.isAnnotationPresent(ManyToOne.class) || value instanceof String) {
            try {
                Class<?> finalType = type;
                return repository.findById((String) value)
                        .orElseThrow(() -> new IllegalArgumentException("Entity " + finalType + " not found for ID: " + value));
            } catch (IllegalArgumentException e) {
                logger.error(e.getMessage());
                throw new RuntimeException("Failed to find entity by ID: " + value, e);
            }
        }

        if (field.isAnnotationPresent(OneToMany.class) || value instanceof List<?>) {
            try {
                List<?> values = (List<?>) value;
                Class<?> finalType = type;
                return values.stream()
                        .map(id -> repository.findById(id.toString())
                                .orElseThrow(() -> new IllegalArgumentException("Entity " + finalType + " not found for ID: " + id)))
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
     * Retrieves the value for a given field name from the input.
     */
    private Object getFieldValue(Object input, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        if (input instanceof Map) {
            return ((Map<String, Object>) input).get(fieldName);
        } else {
            Class<?> clazz = input.getClass();

            while (clazz != null && clazz != Object.class) {
                try {
                    Field field = clazz.getDeclaredField(fieldName);
                    field.setAccessible(true);
                    return field.get(input);
                } catch (NoSuchFieldException e) {
                    clazz = clazz.getSuperclass();
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

            if (genericType instanceof ParameterizedType) {
                Type[] actualTypeArguments = ((ParameterizedType) genericType).getActualTypeArguments();

                if (actualTypeArguments.length == 1 && actualTypeArguments[0] instanceof Class) {
                    return (Class<?>) actualTypeArguments[0];
                }
            }
        }
        return field.getType();
    }

    public <E extends BaseEntity> JpaRepository<E, String> getRepositoryFor(Class<?> entityClass) {
        for (JpaRepository<? extends BaseEntity, String> repo : jpaRepositoryList) {
            ResolvableType type = ResolvableType.forClass(repo.getClass())
                    .as(JpaRepository.class);
            Class<?> domainType = type.getGeneric(0).resolve();

            if (entityClass.equals(domainType)) {
                return (JpaRepository<E, String>) repo;
            }
        }
        throw new IllegalArgumentException("No repository found for " + entityClass.getName());
    }
}
