package org.example.debriefrepository.repository;

import org.example.debriefrepository.entity.Debrief;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

public interface DebriefRepository extends JpaRepository<Debrief, String> {
    List<Debrief> findByDate(ZonedDateTime date);
    List<Debrief> findByGroupName(String name);
    List<Debrief> findByGroup_Id(String id);
    List<Debrief> findByUser_Id(String id);
}