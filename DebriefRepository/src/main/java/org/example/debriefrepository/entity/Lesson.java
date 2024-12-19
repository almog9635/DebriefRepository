package org.example.debriefrepository.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "lesson", schema = "debrief_mgmt")
public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "lesson_id_gen")
    @SequenceGenerator(name = "lesson_id_gen", sequenceName = "lesson_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "content", nullable = false, length = Integer.MAX_VALUE)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "debrief_id", nullable = false)
    private Debrief debrief;

    @OneToMany(mappedBy = "lesson")
    private Set<Mission> missions = new LinkedHashSet<>();

}