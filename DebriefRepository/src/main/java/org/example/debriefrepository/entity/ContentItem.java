package org.example.debriefrepository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Table;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "content_item", schema = "debrief_mgmt")
public class ContentItem extends OrderedItem{

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "debrief_id", nullable = false)
    private Debrief debrief;

}