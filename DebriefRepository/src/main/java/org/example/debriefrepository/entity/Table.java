package org.example.debriefrepository.entity;

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
public class Table extends ContentItem{

    @OneToMany(mappedBy = "table")
    private List<TableColumn> columns = new ArrayList<>();

    @OneToMany(mappedBy = "table")
    private List<Row> rows = new ArrayList<>();

}