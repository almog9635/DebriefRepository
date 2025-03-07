package org.example.debriefrepository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "comment", schema = "debrief_mgmt")
public class Comment extends OrderedItem{

    @Column(name = "bullet", nullable = false)
    private String bullet;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "paragraph_id", nullable = false)
    private Paragraph paragraph;

}