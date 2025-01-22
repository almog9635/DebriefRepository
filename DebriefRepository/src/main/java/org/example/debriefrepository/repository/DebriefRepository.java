package org.example.debriefrepository.repository;

import org.example.debriefrepository.entity.Debrief;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface DebriefRepository extends JpaRepository<Debrief, Long> {
    List<Debrief> findByDate(LocalDate date);
    List<Debrief> findByGroupName(String name);
    List<Debrief> findByGroupId(Long id);
    List<Debrief> findByUserId(Long id);
}