package org.example.debriefrepository.repository;

import org.example.debriefrepository.entity.Mission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.ZonedDateTime;
import java.util.List;

public interface MissionRepository extends JpaRepository<Mission, String> {
    List<Mission> findByStartDate(ZonedDateTime startDate);

    List<Mission> findByDeadline(ZonedDateTime deadline);
}
