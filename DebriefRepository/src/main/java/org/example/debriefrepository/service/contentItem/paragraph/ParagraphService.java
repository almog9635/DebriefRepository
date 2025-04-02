package org.example.debriefrepository.service.contentItem.paragraph;

import lombok.RequiredArgsConstructor;
import org.example.debriefrepository.entity.Comment;
import org.example.debriefrepository.entity.Paragraph;
import org.example.debriefrepository.repository.CommentRepository;
import org.example.debriefrepository.repository.DebriefRepository;
import org.example.debriefrepository.repository.ParagraphRepository;
import org.example.debriefrepository.service.GenericService;
import org.example.debriefrepository.types.content.CommentInput;
import org.example.debriefrepository.types.content.ParagraphInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ParagraphService {

    private final ParagraphRepository paragraphRepository;

    private final CommentRepository commentRepository;

    private final CommentService commentService;

    private final DebriefRepository debriefRepository;

    @Autowired
    private final GenericService<Paragraph, ParagraphInput> genericService;

    private final Logger logger = LoggerFactory.getLogger(ParagraphService.class);

    /* to ask chanan if is it better to set the paragraph input id instead of fetching it manually */
    public Paragraph createParagraph(ParagraphInput paragraphInput, String debriefId) {
        Paragraph paragraph = new Paragraph();
        List<String> skippedFields = new ArrayList<>();
        skippedFields.add("id");
        skippedFields.add("debrief");
        skippedFields.add("comments");
        paragraph = genericService.setFieldsGeneric(paragraph, paragraphInput, null, skippedFields);

        try{
            paragraph.setDebrief(debriefRepository.findById(debriefId)
                    .orElseThrow(() -> new IllegalArgumentException("Debrief not found")));
            paragraphRepository.save(paragraph);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new RuntimeException("Error creating paragraph: " + paragraphInput, e);
        }

        List<CommentInput> comments = paragraphInput.getComments();
        if (Objects.nonNull(comments) && !comments.isEmpty()) {
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
        List<String> skippedFields = new ArrayList<>();
        skippedFields.add("id");
        skippedFields.add("comments");
        paragraph = genericService.setFieldsGeneric(paragraph, paragraphInput, null, skippedFields);
        List<CommentInput> comments = paragraphInput.getComments();

        if (Objects.nonNull(comments) && !comments.isEmpty()) {
            List<Comment> updatedComments = new ArrayList<>(paragraph.getComments());
            for (CommentInput commentInput : comments) {
                Comment existingComment = commentRepository.findById(commentInput.getId())
                        .orElse(null);
                if (Objects.nonNull(existingComment)) {
                    updatedComments.add(commentService.updateComment(commentInput));
                } else{
                    updatedComments.add(commentService.createComment(commentInput, paragraph.getId()));
                }
            }
            paragraph.setComments(updatedComments);
            try{
                paragraph = paragraphRepository.save(paragraph);
            } catch (Exception e) {
                logger.error(e.getMessage());
                throw new RuntimeException("Error creating paragraph: " + paragraphInput, e);
            }
        }
        return paragraph;
    }

}
