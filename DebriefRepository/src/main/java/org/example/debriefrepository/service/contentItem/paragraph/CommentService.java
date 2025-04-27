package org.example.debriefrepository.service.contentItem.paragraph;

import lombok.RequiredArgsConstructor;
import org.example.debriefrepository.entity.Comment;
import org.example.debriefrepository.repository.CommentRepository;
import org.example.debriefrepository.repository.ParagraphRepository;
import org.example.debriefrepository.service.GenericService;
import org.example.debriefrepository.types.content.CommentInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService extends GenericService<Comment, CommentInput> {

    private final CommentRepository commentRepository;

    private final ParagraphRepository paragraphRepository;

    private static final Logger logger = LoggerFactory.getLogger(CommentService.class);

    public Comment createComment(CommentInput input, String paragraphId) {
        Comment comment = new Comment();
        List<String> skippedFields = new ArrayList<>();
        skippedFields.add("id");
        skippedFields.add("paragraph");
        comment = setFields(comment, input, skippedFields);
        try {
            comment.setParagraph(paragraphRepository.findById(paragraphId).
                    orElseThrow(() -> new IllegalArgumentException("Invalid paragraph id: " + paragraphId)));
            return commentRepository.save(comment);
        } catch (Exception e) {
            logger.error("Error creating comment: {}", e.getMessage(), e);
            throw new RuntimeException("Error creating comment", e);
        }
    }

    public Comment updateComment(CommentInput input) {
        String id = input.getId();
        List<String> skippedFields = new ArrayList<>();
        skippedFields.add("id");
        try {
            Comment comment = commentRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Comment not found"));
            comment = setFields(comment, input, skippedFields);
            return commentRepository.save(comment);
        } catch (Exception e) {
            logger.error("Error updating comment: {}", e.getMessage(), e);
            throw new RuntimeException("Error updating comment", e);
        }

    }

    protected Comment setFields(Comment comment, CommentInput input, List<String> skippedFields) {
        return super.setFields(comment, input, null, skippedFields);
    }

}
