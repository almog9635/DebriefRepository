package org.example.debriefrepository.service;

import lombok.RequiredArgsConstructor;
import org.example.debriefrepository.entity.Comment;
import org.example.debriefrepository.entity.Paragraph;
import org.example.debriefrepository.repository.CommentRepository;
import org.example.debriefrepository.repository.DebriefRepository;
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

    private final DebriefRepository debriefRepository;

    private final Logger logger = LoggerFactory.getLogger(ParagraphService.class);

    public Paragraph createParagraph(ParagraphInput paragraphInput, String debriefId) {
        Paragraph paragraph = new Paragraph();
        paragraph = setFields(paragraph, paragraphInput, debriefId);

        try{
            paragraphRepository.save(paragraph);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new RuntimeException("Error creating paragraph: " + paragraphInput, e);
        }

        List<CommentInput> comments = paragraphInput.getComments();
        if (comments != null && !comments.isEmpty()) {
            List<Comment> savedComments = new ArrayList<>();
            for (CommentInput commentInput : comments) {
                Comment comment = commentService.createComment(commentInput, paragraph.getId());
                savedComments.add(comment);
            }
            paragraph.setComments(savedComments);

            try{
                paragraph = paragraphRepository.save(paragraph);
            } catch (Exception e) {
                logger.error(e.getMessage());
                throw new RuntimeException("Error creating paragraph: " + paragraphInput, e);
            }

        }
        return paragraph;
    }

    public Paragraph updateParagraph(ParagraphInput paragraphInput) {
        String paragraphId = paragraphInput.getId();
        Paragraph paragraph = paragraphRepository.findById(paragraphId)
                .orElseThrow(() -> new IllegalArgumentException("Paragraph not found"));

        paragraph = setFields(paragraph, paragraphInput, "");

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

    private Paragraph setFields(Paragraph paragraph, ParagraphInput paragraphInput, String debriefId) {
        paragraph = (Paragraph) super.setFields(paragraph, paragraphInput);
        try{
            if(Objects.isNull(paragraphInput.getName())){
                throw new IllegalArgumentException("Paragraph name is null");
            }
            paragraph.setName(paragraphInput.getName());
            if(Objects.isNull(paragraph.getDebrief())){
                    paragraph.setDebrief(debriefRepository.findById(debriefId)
                            .orElseThrow(() -> new IllegalArgumentException("Error creating Debrief")));
            }
        } catch (IllegalArgumentException e) {
        logger.error(e.getMessage());
        throw new RuntimeException("Error creating paragraph: " + paragraphInput, e);
    }
        return paragraph;
    }

}
