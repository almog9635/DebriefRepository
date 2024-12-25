package org.example.debriefrepository.repository;

import org.example.debriefrepository.entity.Debrief;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DebriefRepository extends JpaRepository<Debrief, Long> {
}