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
public class Paragraph extends ContentItem{

    @OneToMany(mappedBy = "paragraph")
    private List<Comment> comments = new ArrayList<>();

}