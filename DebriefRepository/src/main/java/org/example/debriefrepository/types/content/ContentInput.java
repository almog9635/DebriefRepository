package org.example.debriefrepository.types.content;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class ContentInput {

    private List<ParagraphInput> paragraphs;
    private List<TableInput> tables;

}
