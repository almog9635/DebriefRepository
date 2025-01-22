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
@Table(name = "\"group\"", schema = "debrief_mgmt")
public class Group {

    @Id
    @Column(name = "id", nullable = false, length = Integer.MAX_VALUE)
    private String id = UUID.randomUUID().toString();

    @Column(name = "name", nullable = false, length = Integer.MAX_VALUE)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commander_id")
    private User commander;

    @OneToMany(mappedBy = "group")
    private Set<User> users = new LinkedHashSet<>();

}