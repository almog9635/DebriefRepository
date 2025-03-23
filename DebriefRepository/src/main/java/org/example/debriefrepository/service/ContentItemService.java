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
public class ContentItemService {

    @Autowired
    private final ParagraphService paragraphService;

    // todo: to change the return value
    public List<ContentItem> CreateContentItem(ContentInput contentItemInput) {
        List<ContentItem> contentItems = new ArrayList<>();
        contentItemInput.getParagraphs().forEach(paragraph -> {
            contentItems.add(paragraphService.createParagraph(paragraph));
        });
//        content.add(contentItemInput.getTables().forEach(table -> {
//
//        }));
        return contentItems;
    }

    public List<ContentItem> UpdateContentItem(ContentInput contentItemInput) {
        List<ContentItem> contentItems = new ArrayList<>();
        contentItemInput.getParagraphs().forEach(paragraph -> {
            contentItems.add(paragraphService.updateParagraph(paragraph));
        });

        return contentItems;
    }
}
