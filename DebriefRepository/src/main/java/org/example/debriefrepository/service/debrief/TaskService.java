package org.example.debriefrepository.service.debrief;

import lombok.RequiredArgsConstructor;
import org.example.debriefrepository.entity.Task;
import org.example.debriefrepository.repository.DebriefRepository;
import org.example.debriefrepository.repository.LessonRepository;
import org.example.debriefrepository.repository.TaskRepository;
import org.example.debriefrepository.service.GenericService;
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
public class TaskService extends GenericService<Task, TaskInput> {

    private final LessonRepository lessonRepository;

    private final DebriefRepository debriefRepository;

    private final TaskRepository taskRepository;

    private final Logger logger = LoggerFactory.getLogger(TaskService.class);

    public Task createTask(TaskInput taskInput, String debriefId, String lessonId) {
        Task task = new Task();
        List<String> skippedFields = new ArrayList<>();
        skippedFields.add("id");
        skippedFields.add("debrief");
        skippedFields.add("lesson");
        super.setFields(task, taskInput, null, skippedFields);
        try {
            if (Objects.nonNull(lessonId)) {
                task.setLesson(lessonRepository.findById(lessonId)
                        .orElseThrow(() -> new IllegalArgumentException("lesson not found")));
            }
            task.setDebrief(debriefRepository.findById(debriefId)
                    .orElseThrow(() -> new IllegalArgumentException("debrief not found")));
            return taskRepository.save(task);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public Task updateTask(TaskInput taskInput) {
        String id = taskInput.id();
        try {
            Task existingTask = taskRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("task not found"));
            super.setFields(existingTask, taskInput, null, null);
            return taskRepository.save(existingTask);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
