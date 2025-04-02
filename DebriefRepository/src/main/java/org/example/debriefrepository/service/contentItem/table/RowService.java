package org.example.debriefrepository.service.contentItem.table;

import lombok.RequiredArgsConstructor;
import org.example.debriefrepository.entity.Cell;
import org.example.debriefrepository.entity.Row;
import org.example.debriefrepository.repository.RowRepository;
import org.example.debriefrepository.repository.TableRepository;
import org.example.debriefrepository.service.GenericService;
import org.example.debriefrepository.types.content.CellInput;
import org.example.debriefrepository.types.content.RowInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class RowService {
    
    private final RowRepository rowRepository;
    
    private final TableRepository tableRepository;
    
    @Autowired
    private final GenericService<Row, RowInput> genericService;

    @Autowired
    private final CellService cellService;
    
    private final Logger logger = LoggerFactory.getLogger(RowService.class);
    
    public Row createRow(RowInput input, String tableId) {
        Row row = new Row();
        List<String> skippedFields = new ArrayList<>();
        skippedFields.add("table");
        skippedFields.add("cells");
        genericService.setFieldsGeneric(row, input, null, skippedFields);
        try{
            row.setTable(tableRepository.findById(tableId)
                .orElseThrow(() -> new IllegalArgumentException("row not found")));
            rowRepository.save(row);
            setFields(row, input, null);
            return rowRepository.save(row);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new RuntimeException("Error creating row: " + input, e);
        }
    }

    public Row updateRow(RowInput input) {
        String id = input.getId();
        List<String> skippedFields = new ArrayList<>();
        skippedFields.add("id");
        try{
            Row existingRow = rowRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("row not found"));
            setFields(existingRow, input, skippedFields);
            return rowRepository.save(existingRow);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new RuntimeException("Error updating row: " + input, e);
        }
    }

    private Row setFields(Row row, RowInput input, List<String> skippedFields) {
        Map<String, Function<Object, Object>> customProcessors = new HashMap<>();
        customProcessors.put("cells", rawValue -> {
            if(!((List<CellInput>)rawValue).isEmpty()){
                List<Cell> cells = new ArrayList<>();
                for (CellInput cellInput : (List<CellInput>)rawValue) {
                    if(Objects.isNull(cellInput.getId()) || cellInput.getId().isBlank()){
                        cells.add(cellService.createCell(cellInput));
                    } else {
                        cells.add(cellService.updateCell(cellInput));
                    }
                }
                return cells;
            }

            throw new IllegalArgumentException("Invalid value for cells field");
        });

        return genericService.setFieldsGeneric(row, input, customProcessors, skippedFields);
    }

}
