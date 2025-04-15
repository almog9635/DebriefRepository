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
@DiscriminatorValue("PARAGRAPH")
public class Paragraph extends ContentItem {

    @OneToMany(mappedBy = "paragraph")
    private List<Comment> comments = new ArrayList<>();
}