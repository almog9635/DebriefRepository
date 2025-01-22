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
@Table(name = "role", schema = "debrief_mgmt")
public class Role {

    @Id
    @Column(name = "id", nullable = false, length = Integer.MAX_VALUE)
    private String id = UUID.randomUUID().toString();

    @Column(name = "name", nullable = false, length = Integer.MAX_VALUE)
    private String name;

    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    private Set<User> users = new LinkedHashSet<>();

}