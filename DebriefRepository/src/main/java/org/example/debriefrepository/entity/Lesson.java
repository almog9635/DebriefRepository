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

    @Column(name = "content", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "debrief_id", nullable = false)
    private Debrief debrief;

    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL
            ,fetch = FetchType.LAZY)
    private List<Task> tasks = new ArrayList<>();
}