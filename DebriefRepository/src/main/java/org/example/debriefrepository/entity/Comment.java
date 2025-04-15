package org.example.debriefrepository.entity;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "comments")
public class Comment extends OrderedItem {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "paragraph_id", nullable = false)
    private Paragraph paragraph;

    @Column(name = "bullet", nullable = false)
    private String bullet;
}