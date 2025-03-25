package org.example.debriefrepository.repository;

import org.example.debriefrepository.entity.ContentItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentItemRepository extends JpaRepository<ContentItem, String> {
}
