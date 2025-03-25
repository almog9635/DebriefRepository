package org.example.debriefrepository.service;

import lombok.RequiredArgsConstructor;
import org.example.debriefrepository.entity.Comment;
import org.example.debriefrepository.entity.Paragraph;
import org.example.debriefrepository.repository.CommentRepository;
import org.example.debriefrepository.repository.ParagraphRepository;
import org.example.debriefrepository.types.content.CommentInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService extends OrderedItemService {

    private final CommentRepository commentRepository;
    private final ParagraphRepository paragraphRepository;
    private static final Logger logger = LoggerFactory.getLogger(CommentService.class);

    public Comment createComment(CommentInput input, String paragraphId) {
        Comment comment = new Comment();
        try {
            comment = setFields(comment, input);
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
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));
        comment = setFields(comment, input);
        return commentRepository.save(comment);
    }

    protected Comment setFields(Comment comment, CommentInput input) {
        comment = (Comment) super.setFields(comment, input);

        if (input.getBullet() == null) {
            throw new IllegalArgumentException("Bullet cannot be null");
        }

        comment.setBullet(input.getBullet());
        return comment;
    }

}
