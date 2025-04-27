package org.example.debriefrepository.types.content;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@RequiredArgsConstructor
public class CommentInput extends OrderedItemInput {

    private String bullet;
}
