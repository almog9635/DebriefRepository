package org.example.debriefrepository.entities

import jakarta.persistence.*

@Entity
@Table(name = "role", schema = "debrief_mgmt")
open class Role {
    @Id
    @Column(name = "id", nullable = false)
    open var id: Short? = null

    @Column(name = "role_name", nullable = false, length = Integer.MAX_VALUE)
    open var roleName: String? = null

    @ManyToMany
    @JoinTable(
        name = "user_roles",
        joinColumns = [JoinColumn(name = "role_id")],
        inverseJoinColumns = [JoinColumn(name = "user_id")]
    )
    open var users: MutableSet<org.example.debriefrepository.entities.User> = mutableSetOf()
}