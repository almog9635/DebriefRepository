package org.example.debriefrepository.service.contentItem.table;

import lombok.RequiredArgsConstructor;
import org.example.debriefrepository.entity.TableColumn;
import org.example.debriefrepository.repository.TableColumnRepository;
import org.example.debriefrepository.repository.TableRepository;
import org.example.debriefrepository.service.GenericService;
import org.example.debriefrepository.types.content.ColumnInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TableColumnService {

    private final TableColumnRepository tableColumnRepository;

    private final TableRepository tableRepository;

    @Autowired
    private final GenericService<TableColumn, ColumnInput> genericService;

    private final Logger logger = LoggerFactory.getLogger(TableColumnService.class);

    public TableColumn createColumn(ColumnInput input, String tableId) {
        TableColumn col = new TableColumn();
        List<String> skippedFields = new ArrayList<>();
        skippedFields.add("table");
        genericService.setFields(col, input, null, skippedFields);
        try {
            col.setTable(tableRepository.findById(tableId)
                    .orElseThrow(() -> new IllegalArgumentException("Could not find table with id: " + tableId)));
            return tableColumnRepository.save(col);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public TableColumn updateColumn(ColumnInput input) {
        String id = input.getId();
        TableColumn existingCol = tableColumnRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Could not find table with id: " + id));
        List<String> skippedFields = new ArrayList<>();
        skippedFields.add("id");
        genericService.setFields(existingCol, input, null, skippedFields);
        try {
            return tableColumnRepository.save(existingCol);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
