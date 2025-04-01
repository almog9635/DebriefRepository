package org.example.debriefrepository.entity;

import jakarta.persistence.*;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "debrief", schema = "debrief_mgmt")
public class Debrief extends BaseEntity{

    @Column(name = "date", nullable = false)
    private ZonedDateTime date;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @OneToMany(mappedBy = "debrief")
    private List<ContentItem> contentItems = new ArrayList<>();

    @OneToMany(mappedBy = "debrief")
    private List<Lesson> lessons = new ArrayList<>();

    @OneToMany(mappedBy = "debrief")
    private List<Task> tasks = new ArrayList<>();

}