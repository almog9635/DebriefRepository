package org.example.debriefrepository.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "\"user\"", schema = "debrief_mgmt")
public class User extends BaseEntity {
    
    @Column(name = "last_name", nullable = false, length = Integer.MAX_VALUE)
    private String lastName;

    @Column(name = "service_type", nullable = false, length = Integer.MAX_VALUE)
    private String serviceType;

    @Column(name = "password", nullable = false, length = Integer.MAX_VALUE)
    private String password;

    @Column(name = "rank", nullable = false, length = Integer.MAX_VALUE)
    private String rank;

    @Column(name = "first_name", nullable = false, length = Integer.MAX_VALUE)
    private String firstName;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @OneToMany(mappedBy = "user")
    private List<Debrief> debriefs = new ArrayList<>();

    @OneToMany(mappedBy = "commander")
    private List<Group> groups = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Mission> missions = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserRole> roles = new ArrayList<>();

}