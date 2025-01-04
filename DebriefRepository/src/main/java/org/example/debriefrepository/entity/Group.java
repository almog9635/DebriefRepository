package org.example.debriefrepository.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "\"group\"", schema = "debrief_mgmt")
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "group_id_gen")
    @SequenceGenerator(name = "group_id_gen", sequenceName = "group_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = Integer.MAX_VALUE)
    private String name;

    @OneToMany(mappedBy = "group")
    private Set<Debrief> debriefs = new LinkedHashSet<>();

    @OneToMany(mappedBy = "group")
    private Set<User> users = new LinkedHashSet<>();

}