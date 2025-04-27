package org.example.debriefrepository.repository;

import org.example.debriefrepository.entity.Cell;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CellRepository extends JpaRepository<Cell, String> {
}
