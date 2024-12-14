package org.example.debriefrepository.entities

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "missions", schema = "debrief_mgmt")
open class Mission {
    @Id
    @Column(name = "id", nullable = false)
    open var id: Long? = null

    @Column(name = "content", nullable = false, length = Integer.MAX_VALUE)
    open var content: String? = null

    @Column(name = "start_date", nullable = false)
    open var startDate: LocalDate? = null

    @Column(name = "deadline", nullable = false)
    open var deadline: LocalDate? = null

    @ManyToMany(mappedBy = "missions")
    open var debriefs: MutableSet<Debrief> = mutableSetOf()

    @ManyToMany(mappedBy = "missions")
    open var lessons: MutableSet<Lesson> = mutableSetOf()

    @ManyToMany(mappedBy = "missions")
    open var users: MutableSet<org.example.debriefrepository.entities.User> = mutableSetOf()
}