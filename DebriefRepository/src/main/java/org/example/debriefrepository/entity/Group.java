package org.example.debriefrepository.entity;

import jakarta.persistence.*;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "group", schema = "debrief_mgmt")
public class Group extends BaseEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commander_id")
    private User commander;

    @OneToMany(mappedBy = "group")
    private List<Debrief> debriefs = new ArrayList<>();

    @OneToMany(mappedBy = "group")
    private List<User> users = new ArrayList<>();

}