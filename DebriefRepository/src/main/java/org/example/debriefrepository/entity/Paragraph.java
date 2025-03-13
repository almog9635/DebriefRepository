package org.example.debriefrepository.entity;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@DiscriminatorValue("PARAGRAPH")
//@Table(name = "paragraph", schema = "debrief_mgmt")
public class Paragraph extends ContentItem{

    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "paragraph")
    private List<Comment> comments = new ArrayList<>();

}