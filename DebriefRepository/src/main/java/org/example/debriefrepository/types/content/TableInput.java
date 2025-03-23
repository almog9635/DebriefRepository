package org.example.debriefrepository.types.content;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class TableInput extends ContentItem {

    List<ColumnInput> cols;
    List<RowInput> rows;

}
