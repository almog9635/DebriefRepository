package org.example.debriefrepository.repository;

import org.example.debriefrepository.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.ZonedDateTime;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, String> {
    List<Task> findByStartDate(ZonedDateTime startDate);

    List<Task> findByDeadline(ZonedDateTime deadline);
}
