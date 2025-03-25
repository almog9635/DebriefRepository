package org.example.debriefrepository.service;

import jakarta.persistence.Column;
import lombok.RequiredArgsConstructor;
import org.example.debriefrepository.entity.Comment;
import org.example.debriefrepository.entity.OrderedItem;
import org.example.debriefrepository.types.content.CommentInput;
import org.example.debriefrepository.types.content.OrderedItemInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderedItemService {

    private static final Logger logger = LoggerFactory.getLogger(OrderedItemService.class);

    protected OrderedItem setFields(OrderedItem item, OrderedItemInput input) {
        List<Field> fields = getAllFields(OrderedItem.class);

        for (Field field : fields) {
            field.setAccessible(true);
            String fieldName = field.getName();

            if(fieldName.equals("id") || fieldName.equals("metaData"))
                continue;

            try{
                Object value = getFieldValue(input, fieldName);
                field.setAccessible(true);

                if(Objects.isNull(value)) {
                    Column annotation = field.getAnnotation(Column.class);
                    boolean isNullable = !(Objects.isNull(annotation)) &&  annotation.nullable();

                    if(!isNullable && !Objects.isNull(item.getClass().getField(fieldName))) {
                        throw new IllegalArgumentException("Field '" + fieldName + "' cannot be null");
                    }
                }

                field.set(item, value);
            } catch (NoSuchFieldException e) {
                throw new RuntimeException("Field " + fieldName + " not found");
            }
            catch (IllegalArgumentException | IllegalAccessException e) {
                logger.error("Unable to access field {} in orderedItem entity", fieldName, e);
                throw new RuntimeException("Failed to update orderedItem field: " + fieldName, e);
            }
        }

        return item;
    }

    protected List<Field> getAllFields(Class clazz){
        if (clazz == null) {
            return Collections.emptyList();
        }

        List<Field> result = new ArrayList<>(getAllFields(clazz.getSuperclass()));
        List<Field> filteredFields = Arrays.stream(clazz.getDeclaredFields())
                .collect(Collectors.toList());
        result.addAll(filteredFields);
        return result;
    }

    public static Object getFieldValue(Object target, String fieldName) {
        Class<?> clazz = target.getClass();

        while (clazz != null && clazz != Object.class) {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true); // Access private field
                return field.get(target);  // Return the value
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass(); // Move to superclass
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Cannot access field: " + fieldName, e);
            }
        }

        throw new IllegalArgumentException("Field '" + fieldName + "' not found in object of type: " + target.getClass().getName());
    }

}
