package org.example.debriefrepository.service.debrief;

import lombok.RequiredArgsConstructor;
import org.example.debriefrepository.entity.Lesson;
import org.example.debriefrepository.entity.Task;
import org.example.debriefrepository.repository.DebriefRepository;
import org.example.debriefrepository.repository.LessonRepository;
import org.example.debriefrepository.service.GenericService;
import org.example.debriefrepository.types.input.LessonInput;
import org.example.debriefrepository.types.input.TaskInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class LessonService {

    private final LessonRepository lessonRepository;

    private final DebriefRepository debriefRepository;

    private final TaskService taskService;

    @Autowired
    private GenericService<Lesson, LessonInput> genericService;

    private final Logger logger = LoggerFactory.getLogger(LessonService.class);

    public Lesson createLesson(LessonInput input, String debriefId) {
        Lesson lesson = new Lesson();
        List<String> skippedFields = new ArrayList<>();
        skippedFields.add("id");
        skippedFields.add("debrief");
        skippedFields.add("tasks");
        genericService.setFields(lesson, input, null, skippedFields);
        try {
            lesson.setDebrief(debriefRepository.findById(debriefId)
                    .orElseThrow(() -> new IllegalArgumentException("debrief not found")));
            lessonRepository.save(lesson);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
        List<TaskInput> tasks = input.tasks();
        List<Task> savedTasks = new ArrayList<>();
        if (!tasks.isEmpty()) {
            for (TaskInput task : tasks) {
                savedTasks.add(taskService.createTask(task, debriefId, lesson.getId()));
            }
            lesson.setTasks(savedTasks);
            try {
                return lessonRepository.save(lesson);
            } catch (Exception e) {
                logger.error(e.getMessage());
                throw new RuntimeException(e);
            }
        }
        throw new RuntimeException("tasks can not be empty");
    }

    public Lesson updateLesson(LessonInput input) {
        String id = input.id();
        List<String> skippedFields = new ArrayList<>();
        skippedFields.add("tasks");
        Lesson existingLesson = null;
        try {
            existingLesson = lessonRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("debrief not found"));
            genericService.setFields(existingLesson, input, null, skippedFields);

        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
        List<Task> savedTasks = new ArrayList<>();
        if (!input.tasks().isEmpty()) {
            for (TaskInput task : input.tasks()) {
                if (Objects.isNull(task.id()) || task.id().isBlank()) {
                    savedTasks.add(taskService.createTask(task, existingLesson.getDebrief().getId(), id));
                } else {
                    savedTasks.add(taskService.updateTask(task));
                }
            }
            existingLesson.setTasks(savedTasks);
        }
        try {
            return lessonRepository.save(existingLesson);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
