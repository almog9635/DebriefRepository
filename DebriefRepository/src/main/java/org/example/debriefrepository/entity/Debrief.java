package org.example.debriefrepository.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "debrief", schema = "debrief_mgmt")
public class Debrief {

    @Id
    @Column(name = "id", nullable = false, length = Integer.MAX_VALUE)
    private String id;

    @Column(name = "date", nullable = false)
    private ZonedDateTime date;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @Column(name = "content", nullable = false, length = Integer.MAX_VALUE)
    private String content;

    @OneToMany(mappedBy = "debrief")
    private List<Lesson> lessons = new ArrayList<>();

    @OneToMany(mappedBy = "debrief")
    private List<Mission> missions = new ArrayList<>();

}