package org.example.debriefrepository.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@jakarta.persistence.Table(name = "\"column\"", schema = "debrief_mgmt")
public class Column extends OrderedItem {

    @jakarta.persistence.Column(name = "name", nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "table_id")
    private Table table;

    @OneToMany(mappedBy = "column")
    private List<Cell> cells = new ArrayList<>();

}