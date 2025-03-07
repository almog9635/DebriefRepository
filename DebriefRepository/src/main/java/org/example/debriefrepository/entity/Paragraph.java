package org.example.debriefrepository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "paragraph", schema = "debrief_mgmt")
public class Paragraph extends OrderedItem{

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "debrief_id", nullable = false)
    private String debriefId;

    @OneToMany(mappedBy = "paragraph")
    private List<Comment> comments = new ArrayList<>();

}