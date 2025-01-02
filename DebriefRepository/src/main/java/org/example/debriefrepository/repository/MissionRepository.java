package org.example.debriefrepository.repository;

import org.example.debriefrepository.entity.Mission
;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MissionRepository extends JpaRepository<Mission
, Long> {
}
