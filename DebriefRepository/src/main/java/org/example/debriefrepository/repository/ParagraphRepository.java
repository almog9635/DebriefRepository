package org.example.debriefrepository.repository;

import org.example.debriefrepository.entity.Paragraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParagraphRepository extends JpaRepository<Paragraph, String> {
}
