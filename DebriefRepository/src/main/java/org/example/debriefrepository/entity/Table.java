package org.example.debriefrepository.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@jakarta.persistence.Table(name = "\"table\"", schema = "debrief_mgmt")
public class Table extends OrderedItem {

    @jakarta.persistence.Column(name = "name", nullable = false)
    private String name;
    

    @jakarta.persistence.Column(name = "debrief_id", nullable = false)
    private String debriefId;

    @OneToMany(mappedBy = "table")
    private List<Column> columns = new ArrayList<>();

    @OneToMany(mappedBy = "table")
    private List<Row> rows = new ArrayList<>();

}