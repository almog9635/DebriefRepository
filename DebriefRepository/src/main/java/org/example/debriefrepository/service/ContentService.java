package org.example.debriefrepository.service;

import lombok.RequiredArgsConstructor;
import org.example.debriefrepository.entity.ContentItem;
import org.example.debriefrepository.types.content.ContentInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContentService {

    @Autowired
    private final ParagraphService paragraphService;

    // todo: create the table row and column services
    public List<ContentItem> createContent(ContentInput contentItemInput, String debriefId) {
        List<ContentItem> contentItems = new ArrayList<>();
        contentItemInput.getParagraphs().forEach(paragraph -> {
            contentItems.add(paragraphService.createParagraph(paragraph, debriefId));
        });
//        contentItems.add(contentItemInput.getTables().forEach(table -> {
//
//        }));
        return contentItems;
    }

    public List<ContentItem> updateContent(ContentInput contentItemInput) {
        List<ContentItem> contentItems = new ArrayList<>();
        contentItemInput.getParagraphs().forEach(paragraph -> {
            contentItems.add(paragraphService.updateParagraph(paragraph));
        });

        return contentItems;
    }

}
