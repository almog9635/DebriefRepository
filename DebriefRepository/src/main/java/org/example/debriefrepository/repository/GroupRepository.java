package org.example.debriefrepository.repository;

import org.example.debriefrepository.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<Group, String> {
    Group findByName(String name);
}
