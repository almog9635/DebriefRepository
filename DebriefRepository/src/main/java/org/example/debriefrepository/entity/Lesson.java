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
@Table(name = "lesson", schema = "debrief_mgmt")
public class Lesson extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "debrief_id", nullable = false)
    private Debrief debrief;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "cluster", nullable = false)
    private String cluster;

    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL
            ,fetch = FetchType.LAZY)
    private List<Task> tasks = new ArrayList<>();
}