package org.example.debriefrepository.types.content;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class ParagraphInput extends ContentItemInput {

    List<CommentInput> comments;

}
