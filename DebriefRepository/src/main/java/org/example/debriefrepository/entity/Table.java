package org.example.debriefrepository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@DiscriminatorValue("TABLE")
//@jakarta.persistence.Table(name = "\"table\"", schema = "debrief_mgmt")
public class Table extends ContentItem{

    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "table")
    private List<TableColumn> cols = new ArrayList<>();

    @OneToMany(mappedBy = "table")
    private List<Row> rows = new ArrayList<>();

}