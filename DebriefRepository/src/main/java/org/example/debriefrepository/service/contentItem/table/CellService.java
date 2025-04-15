package org.example.debriefrepository.service.contentItem.table;

import lombok.RequiredArgsConstructor;
import org.example.debriefrepository.entity.Cell;
import org.example.debriefrepository.repository.CellRepository;
import org.example.debriefrepository.service.GenericService;
import org.example.debriefrepository.types.content.CellInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CellService {

    private final CellRepository cellRepository;

    @Autowired
    private final GenericService<Cell, CellInput> genericService;

    private final Logger logger = LoggerFactory.getLogger(CellService.class);

    public Cell createCell(CellInput input) {
        Cell cell = new Cell();
        cell = genericService.setFields(cell, input, null, null);
        try {
            return cellRepository.save(cell);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public Cell updateCell(CellInput input) {
        String id = input.getId();
        List<String> skippedFields = new ArrayList<>();
        skippedFields.add("id");
        try {
            Cell existingCell = cellRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid cell id: " + id));
            genericService.setFields(existingCell, input, null, skippedFields);
            return cellRepository.save(existingCell);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

}
