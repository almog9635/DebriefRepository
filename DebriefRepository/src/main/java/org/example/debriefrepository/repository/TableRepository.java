package org.example.debriefrepository.repository;

import org.example.debriefrepository.entity.Table;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TableRepository extends JpaRepository<Table, String> {
}
