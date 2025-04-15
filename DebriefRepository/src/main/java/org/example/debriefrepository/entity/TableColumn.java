package org.example.debriefrepository.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@jakarta.persistence.Table(name = "table_columns")
public class TableColumn extends OrderedItem {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "table_id")
    private Table table;

    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "column")
    private List<Cell> cells = new ArrayList<>();
}