package org.example.debriefrepository.types.content;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@RequiredArgsConstructor
public class ColumnInput extends OrderedItemInput{

    private String name;

}
