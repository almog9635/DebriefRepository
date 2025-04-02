package org.example.debriefrepository.entity;

import jakarta.persistence.*;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.example.debriefrepository.types.enums.RankEnum;
import org.example.debriefrepository.types.enums.ServiceTypeEnum;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "user", schema = "debrief_mgmt")
public class User extends BaseEntity {
    
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "service_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ServiceTypeEnum serviceType;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "rank", nullable = false)
    @Enumerated(EnumType.STRING)
    private RankEnum rank;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @OneToMany(mappedBy = "user")
    private List<Debrief> debriefs = new ArrayList<>();

    @OneToMany(mappedBy = "commander")
    private List<Group> groups = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Task> tasks = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserRole> roles = new ArrayList<>();

}