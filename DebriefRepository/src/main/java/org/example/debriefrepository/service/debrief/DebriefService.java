package org.example.debriefrepository.service.debrief;

import lombok.RequiredArgsConstructor;
import org.example.debriefrepository.entity.ContentItem;
import org.example.debriefrepository.entity.Debrief;
import org.example.debriefrepository.entity.Lesson;
import org.example.debriefrepository.entity.Task;
import org.example.debriefrepository.repository.DebriefRepository;
import org.example.debriefrepository.service.GenericService;
import org.example.debriefrepository.service.contentItem.paragraph.ParagraphService;
import org.example.debriefrepository.service.contentItem.table.TableService;
import org.example.debriefrepository.types.content.ContentInput;
import org.example.debriefrepository.types.content.ParagraphInput;
import org.example.debriefrepository.types.content.TableInput;
import org.example.debriefrepository.types.input.DebriefInput;
import org.example.debriefrepository.types.input.LessonInput;
import org.example.debriefrepository.types.input.TaskInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class DebriefService {

    @Autowired
    private final DebriefRepository debriefRepository;

    @Autowired
    private final ParagraphService paragraphService;

    @Autowired
    private final LessonService lessonService;

    @Autowired
    private final TableService tableService;

    @Autowired
    private final TaskService taskService;

    @Autowired
    private GenericService<Debrief, DebriefInput> genericService;

    private static final Logger logger = LoggerFactory.getLogger(DebriefService.class);

    public Debrief createDebrief(DebriefInput input) {
        Debrief debrief = new Debrief();
        try {
            List<String> skippedFields = new ArrayList<>();
            skippedFields.add("id");
            skippedFields.add("contentItems");
            skippedFields.add("lessons");
            skippedFields.add("tasks");
            debrief = setFields(debrief, input, skippedFields);
            debriefRepository.save(debrief);
            skippedFields.remove("contentItems");
            skippedFields.remove("lessons");
            skippedFields.remove("tasks");
            setFields(debrief, input, skippedFields);
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
        if (debriefRepository.existsById(id)) {
            try {
                debriefRepository.deleteById(id);
            } catch (Exception e) {
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
            List<String> skippedFields = new ArrayList<>();
            skippedFields.add("id");
            Debrief existingDebrief = debriefRepository.findById(debriefId)
                    .orElseThrow(() -> new IllegalArgumentException("Debrief not found with ID: " + debriefId));
            return debriefRepository.save(setFields(existingDebrief, debriefUpdate, skippedFields));
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        throw new RuntimeException("Error modifying user");
    }


    private Debrief setFields(Debrief debrief, DebriefInput input, List<String> skippFields) {
        Map<String, Function<Object, Object>> customProcessors = new HashMap<>();
        customProcessors.put("contentItems", rawValue -> {
            if (rawValue instanceof ContentInput contentInput) {
                List<ContentItem> items = new ArrayList<>();

                // Process Paragraphs
                if (Objects.nonNull(contentInput.getParagraphs())) {
                    for (ParagraphInput pInput : contentInput.getParagraphs()) {
                        if (Objects.isNull(pInput.getId()) || pInput.getId().isBlank()) {
                            items.add(paragraphService.createParagraph(pInput, debrief.getId()));
                        } else {
                            items.add(paragraphService.updateParagraph(pInput));
                        }
                    }
                }

                // Process Tables similarly, if applicable
                if (Objects.nonNull(contentInput.getTables())) {
                    for (TableInput tInput : contentInput.getTables()) {
                        if (Objects.isNull(tInput.getId()) || tInput.getId().isBlank()) {
                            items.add(tableService.createTable(tInput, debrief.getId()));
                        } else {
                            items.add(tableService.updateTable(tInput));
                        }
                    }
                }
                return items;
            }
            throw new IllegalArgumentException("Invalid value for contentItems field");
        });

        customProcessors.put("tasks", value -> {
            if (!((List<TaskInput>) value).isEmpty()) {
                List<Task> tasks = new ArrayList<>();
                for (TaskInput taskInput : ((List<TaskInput>) value)) {
                    if (Objects.isNull(taskInput.id()) || taskInput.id().isBlank()) {
                        tasks.add(taskService.createTask(taskInput, debrief.getId(), null));
                    } else {
                        tasks.add(taskService.updateTask(taskInput));
                    }
                }
                return tasks;
            }
            throw new IllegalArgumentException("Invalid value for tasks field");
        });

        customProcessors.put("lessons", value -> {
            if (!((List<LessonInput>) value).isEmpty()) {
                List<Lesson> lessons = new ArrayList<>();
                for (LessonInput lessonInput : ((List<LessonInput>) value)) {
                    if (Objects.isNull(lessonInput.id()) || lessonInput.id().isBlank()) {
                        lessons.add(lessonService.createLesson(lessonInput, debrief.getId()));
                    } else {
                        lessons.add(lessonService.updateLesson(lessonInput));
                    }
                }
                return lessons;
            }
            throw new IllegalArgumentException("Invalid value for lessons field");
        });

        return genericService.setFields(debrief, input, customProcessors, skippFields);
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
