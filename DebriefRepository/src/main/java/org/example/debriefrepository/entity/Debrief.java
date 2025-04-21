package org.example.debriefrepository.entity;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "debrief")
public class Debrief extends BaseEntity {

    @Column(name = "date", nullable = false)
    private ZonedDateTime date;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "labels", nullable = false)
    private String labels;

    @OneToMany(mappedBy = "debrief", cascade = CascadeType.ALL,
            fetch = FetchType.LAZY, orphanRemoval = true)
    private List<ContentItem> contentItems = new ArrayList<>();

    @OneToMany(mappedBy = "debrief", cascade = CascadeType.ALL,
            fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Lesson> lessons = new ArrayList<>();

    @OneToMany(mappedBy = "debrief", cascade = CascadeType.ALL,
            fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Task> tasks = new ArrayList<>();
}