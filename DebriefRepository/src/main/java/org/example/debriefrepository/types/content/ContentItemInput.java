package org.example.debriefrepository.types.content;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "contentType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ParagraphInput.class, name = "TABLE"),
        @JsonSubTypes.Type(value = TableInput.class, name = "PARAGRAPH")
})
public class ContentItemInput extends OrderedItemInput {

    private String name;

}
