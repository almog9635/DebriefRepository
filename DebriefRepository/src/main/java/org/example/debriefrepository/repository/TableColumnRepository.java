package org.example.debriefrepository.repository;

import org.example.debriefrepository.entity.TableColumn;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TableColumnRepository extends JpaRepository<TableColumn, String> {
}
