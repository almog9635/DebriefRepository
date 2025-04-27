package org.example.debriefrepository.service.contentItem.table;

import lombok.RequiredArgsConstructor;
import org.example.debriefrepository.entity.Row;
import org.example.debriefrepository.entity.Table;
import org.example.debriefrepository.entity.TableColumn;
import org.example.debriefrepository.repository.DebriefRepository;
import org.example.debriefrepository.repository.RowRepository;
import org.example.debriefrepository.repository.TableColumnRepository;
import org.example.debriefrepository.repository.TableRepository;
import org.example.debriefrepository.service.GenericService;
import org.example.debriefrepository.types.content.ColumnInput;
import org.example.debriefrepository.types.content.RowInput;
import org.example.debriefrepository.types.content.TableInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TableService extends GenericService<Table, TableInput> {

    @Autowired
    private final TableRepository tableRepository;

    @Autowired
    private final DebriefRepository debriefRepository;

    @Autowired
    private final TableColumnService tableColumnService;

    private final TableColumnRepository tableColumnRepository;

    @Autowired
    private final RowService rowService;

    private final RowRepository rowRepository;

    private final Logger logger = LoggerFactory.getLogger(TableService.class);

    public Table createTable(TableInput input, String debriefId) {
        Table table = new Table();
        List<String> skippedFields = new ArrayList<>();
        skippedFields.add("id");
        skippedFields.add("debrief");
        skippedFields.add("columns");
        skippedFields.add("rows");
        table = super.setFields(table, input, null, skippedFields);
        try {
            table.setDebrief(debriefRepository.findById(debriefId)
                    .orElseThrow(() -> new IllegalArgumentException("Debrief not found")));
            tableRepository.save(table);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new RuntimeException("Error creating table: " + input, e);
        }
        List<ColumnInput> cols = input.getColumns();
        List<RowInput> rows = input.getRows();
        if (Objects.nonNull(cols) && !cols.isEmpty() &&
                Objects.nonNull(rows) && !rows.isEmpty()) {
            List<TableColumn> savedCols = new ArrayList<>();
            for (ColumnInput columnInput : cols) {
                TableColumn newColumn = tableColumnService.createColumn(columnInput, table.getId());
                savedCols.add(newColumn);
            }
            table.setColumns(savedCols);
            List<Row> savedRows = new ArrayList<>();
            for (RowInput rowInput : rows) {
                Row newRow = rowService.createRow(rowInput, table.getId());
                savedRows.add(newRow);
            }
            table.setRows(savedRows);
            try {
                return tableRepository.save(table);
            } catch (Exception e) {
                logger.error(e.getMessage());
                throw new RuntimeException("Error creating table: " + input, e);
            }
        }
        throw new RuntimeException("rows or cols can not be empty: " + input);
    }

    /* todo: change the function that rows or cols can be null */
    public Table updateTable(TableInput input) {
        String id = input.getId();
        Table existingTable = tableRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Paragraph not found"));
        List<String> skippedFields = new ArrayList<>();
        skippedFields.add("id");
        skippedFields.add("columns");
        skippedFields.add("rows");
        existingTable = super.setFields(existingTable, input, null, skippedFields);
        List<ColumnInput> cols = input.getColumns();
        List<RowInput> rows = input.getRows();
        if (Objects.nonNull(cols) && !cols.isEmpty() &&
                Objects.nonNull(rows) && !rows.isEmpty()) {
            List<TableColumn> savedCols = new ArrayList<>();
            for (ColumnInput columnInput : cols) {
                TableColumn existingCol = tableColumnRepository.findById(columnInput.getId())
                        .orElse(null);
                if (Objects.nonNull(existingCol)) {
                    savedCols.add(tableColumnService.updateColumn(columnInput));
                } else {
                    savedCols.add(tableColumnService.createColumn(columnInput, existingTable.getId()));
                }
            }
            existingTable.setColumns(savedCols);
            List<Row> savedRows = new ArrayList<>();
            for (RowInput rowInput : rows) {
                Row existingRow = rowRepository.findById(rowInput.getId())
                        .orElse(null);
                if (Objects.nonNull(existingRow)) {
                    savedRows.add(rowService.updateRow(rowInput));
                } else {
                    savedRows.add(rowService.createRow(rowInput, existingTable.getId()));
                }
            }
            existingTable.setRows(savedRows);
            try {
                return tableRepository.save(existingTable);
            } catch (Exception e) {
                logger.error(e.getMessage());
                throw new RuntimeException("Error creating table: " + input, e);
            }
        }
        throw new RuntimeException("row or cols can not be empty: " + input);
    }

}
