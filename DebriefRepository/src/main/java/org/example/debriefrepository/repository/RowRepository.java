package org.example.debriefrepository.repository;

import org.example.debriefrepository.entity.Row;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RowRepository extends JpaRepository<Row, String> {
}
