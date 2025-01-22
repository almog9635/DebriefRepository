package org.example.debriefrepository.repository;

import org.example.debriefrepository.entity.Mission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface MissionRepository extends JpaRepository<Mission, Long> {
    List<Mission> findByStartDate(LocalDate startDate);

    List<Mission> findByDeadline(LocalDate deadline);
}
