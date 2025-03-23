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
        List<Field> fields = getAllFields(item.getClass());

        for (Field field : fields) {
            field.setAccessible(true);
            String fieldName = field.getName();

            if(fieldName.equals("id") | fieldName.equals("MetaData"))
                continue;

            try{
                Object value = input.getClass().getDeclaredField(fieldName);
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
                logger.error("Unable to access field {} in User entity", fieldName, e);
                throw new RuntimeException("Failed to update user field: " + fieldName, e);
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

    protected Object getFieldValue(Object input, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        if (input instanceof Map) {
            return ((Map<String, Object>) input).get(fieldName); // Get value from Map
        } else {
            Field inputField = input.getClass().getDeclaredField(fieldName);
            inputField.setAccessible(true);
            return inputField.get(input); // Get value via reflection
        }
    }

}
