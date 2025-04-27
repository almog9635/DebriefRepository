package org.example.debriefrepository.repository;

import org.example.debriefrepository.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, String> {
}
