package org.example.debriefrepository.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "debrief", schema = "debrief_mgmt")
public class Debrief {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "debrief_id_gen")
    @SequenceGenerator(name = "debrief_id_gen", sequenceName = "debrief_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "content", nullable = false, length = Integer.MAX_VALUE)
    private String content;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @OneToMany(mappedBy = "debrief")
    private Set<Lesson> lessons = new LinkedHashSet<>();

    @OneToMany(mappedBy = "debrief")
    private Set<Mission> missions = new LinkedHashSet<>();

}