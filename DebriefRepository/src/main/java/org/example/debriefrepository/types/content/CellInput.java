package org.example.debriefrepository.types.content;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class CellInput {

    private String id;
    private String value;
    private String column;
    private String row;

}
