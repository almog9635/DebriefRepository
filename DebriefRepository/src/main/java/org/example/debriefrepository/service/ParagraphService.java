package org.example.debriefrepository.service;

import lombok.RequiredArgsConstructor;
import org.example.debriefrepository.entity.Comment;
import org.example.debriefrepository.entity.Paragraph;
import org.example.debriefrepository.repository.CommentRepository;
import org.example.debriefrepository.repository.ParagraphRepository;
import org.example.debriefrepository.types.content.CommentInput;
import org.example.debriefrepository.types.content.ParagraphInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ParagraphService extends OrderedItemService {

    private final ParagraphRepository paragraphRepository;

    private final CommentRepository commentRepository;

    private final CommentService commentService;

    private final Logger logger = LoggerFactory.getLogger(ParagraphService.class);

    public Paragraph createParagraph(ParagraphInput paragraphInput) {
        Paragraph paragraph = new Paragraph();
        // Copy common fields using reflection via the superclass method.
        paragraph = setFields(paragraph, paragraphInput);
        // Persist initial paragraph to generate an ID (if needed)
        paragraph = paragraphRepository.save(paragraph);

        List<CommentInput> comments = paragraphInput.getComments();
        if (comments != null && !comments.isEmpty()) {
            List<Comment> savedComments = new ArrayList<>();
            for (CommentInput commentInput : comments) {
                Comment comment = commentService.createComment(commentInput, paragraph.getId());
                savedComments.add(comment);
            }
            paragraph.setComments(savedComments);
            paragraph = paragraphRepository.save(paragraph);
        }
        return paragraph;
    }

    public Paragraph updateParagraph(ParagraphInput paragraphInput) {
        String paragraphId = paragraphInput.getId();
        Paragraph paragraph = paragraphRepository.findById(paragraphId)
                .orElseThrow(() -> new IllegalArgumentException("Paragraph not found"));

        paragraph = setFields(paragraph, paragraphInput);

        List<CommentInput> comments = paragraphInput.getComments();
        if (Objects.nonNull(comments) && !comments.isEmpty()) {
            List<Comment> updatedComments = new ArrayList<>(paragraph.getComments());
            for (CommentInput commentInput : comments) {
                Comment existingComment = commentRepository.findById(commentInput.getId())
                        .orElse(null);
                if (Objects.nonNull(existingComment)) {
                    existingComment = commentService.updateComment(commentInput);
                } else{
                    existingComment = commentService.createComment(commentInput, paragraph.getId());
                }
                updatedComments.add(existingComment);
            }
            paragraph.setComments(updatedComments);
        }
        return paragraphRepository.save(paragraph);
    }

    protected Paragraph setFields(Paragraph paragraph, ParagraphInput paragraphInput) {
        // Delegate the common field copying to the parent class.
        paragraph = (Paragraph) super.setFields(paragraph, paragraphInput);
        // Set additional Paragraph-specific fields.
        paragraph.setName(paragraphInput.getName());
        return paragraph;
    }

}
