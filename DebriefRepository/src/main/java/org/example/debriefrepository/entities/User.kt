package org.example.debriefrepository.entities

import jakarta.persistence.*

@Entity
@Table(name = "users", schema = "debrief_mgmt")
open class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_id_gen")
    @SequenceGenerator(name = "users_id_gen", sequenceName = "users_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    open var id: Long? = null

    @Column(name = "first_name", nullable = false, length = Integer.MAX_VALUE)
    open var firstName: String? = null

    @Column(name = "last_name", nullable = false, length = Integer.MAX_VALUE)
    open var lastName: String? = null

    @Column(name = "service_type", nullable = false, length = Integer.MAX_VALUE)
    open var serviceType: String? = null

    @ManyToMany(mappedBy = "users")
    open var roles: MutableSet<Role> = mutableSetOf()

    @ManyToMany(mappedBy = "users")
    open var debriefs: MutableSet<Debrief> = mutableSetOf()

    @ManyToMany(mappedBy = "users")
    open var groups: MutableSet<Group> = mutableSetOf()
}