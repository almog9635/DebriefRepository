package org.example.debriefrepository.service.debrief;

import lombok.RequiredArgsConstructor;
import org.example.debriefrepository.entity.Lesson;
import org.example.debriefrepository.entity.Task;
import org.example.debriefrepository.repository.DebriefRepository;
import org.example.debriefrepository.repository.LessonRepository;
import org.example.debriefrepository.service.GenericService;
import org.example.debriefrepository.types.input.LessonInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LessonService {

    private final LessonRepository lessonRepository;

    private final DebriefRepository debriefRepository;

    @Autowired
    private GenericService<Lesson, LessonInput> genericService;

    private final Logger logger = LoggerFactory.getLogger(LessonService.class);

    public Lesson createLesson(LessonInput input, String debriefId) {
        Lesson lesson = new Lesson();
        List<String> skippedFields = new ArrayList<>();
        skippedFields.add("id");
        skippedFields.add("debrief");
        genericService.setFieldsGeneric(lesson,input,null,skippedFields);
        try{
            lesson.setDebrief(debriefRepository.findById(debriefId)
                    .orElseThrow(() -> new IllegalArgumentException("debrief not found")));
            return lessonRepository.save(lesson);
        } catch (Exception e){
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public Lesson updateLesson(LessonInput input) {
        String id = input.id();
        try{
            Lesson existingLesson = lessonRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("debrief not found"));
            genericService.setFieldsGeneric(existingLesson,input,null,null);
            return lessonRepository.save(existingLesson);
        } catch (Exception e){
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
