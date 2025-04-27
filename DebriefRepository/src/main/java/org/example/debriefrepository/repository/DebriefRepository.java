package org.example.debriefrepository.repository;

import org.example.debriefrepository.entity.Debrief;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.ZonedDateTime;
import java.util.List;

public interface DebriefRepository extends JpaRepository<Debrief, String> {

    List<Debrief> findByDate(ZonedDateTime date);
}