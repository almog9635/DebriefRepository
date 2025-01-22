package org.example.debriefrepository.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "lesson", schema = "debrief_mgmt")
public class Lesson {

    @Id
    @Column(name = "id", nullable = false, length = Integer.MAX_VALUE)
    private String id = UUID.randomUUID().toString();

    @Column(name = "content", nullable = false, length = Integer.MAX_VALUE)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "debrief_id", nullable = false)
    private Debrief debrief;

    @OneToMany(mappedBy = "lesson")
    private Set<Mission> missions = new LinkedHashSet<>();

}