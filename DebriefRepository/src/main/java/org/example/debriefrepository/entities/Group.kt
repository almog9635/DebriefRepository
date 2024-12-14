package org.example.debriefrepository.entities

import jakarta.persistence.*

@Entity
@Table(name = "\"group\"", schema = "debrief_mgmt")
open class Group {
    @Id
    @Column(name = "id", nullable = false)
    open var id: Long? = null

    @Column(name = "name", nullable = false, length = Integer.MAX_VALUE)
    open var name: String? = null

    @ManyToMany(mappedBy = "groups")
    open var debriefs: MutableSet<Debrief> = mutableSetOf()

    @ManyToMany(mappedBy = "groups")
    open var users: MutableSet<org.example.debriefrepository.entities.User> = mutableSetOf()
}