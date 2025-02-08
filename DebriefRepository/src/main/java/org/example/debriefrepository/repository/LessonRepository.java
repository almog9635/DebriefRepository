package org.example.debriefrepository.repository;

import org.example.debriefrepository.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
}
