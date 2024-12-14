package org.example.debriefrepository.repository;

import org.example.debriefrepository.entities.Debrief;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DebriefRepository extends JpaRepository<Debrief, Long> {
}

