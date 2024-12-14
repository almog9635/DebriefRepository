package org.example.debriefrepository.entities

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "debrief", schema = "debrief_mgmt")
open class Debrief {
    @Id
    @Column(name = "id", nullable = false)
    open var id: Long? = null

    @Column(name = "content", nullable = false, length = Integer.MAX_VALUE)
    open var content: String? = null

    @Column(name = "date", nullable = false)
    open var date: LocalDate? = null

    @ManyToMany
    @JoinTable(
        joinColumns = [JoinColumn(name = "debrief_id")],
        inverseJoinColumns = [JoinColumn(name = "lesson_id")]
    )
    open var lessons: MutableSet<org.example.debriefrepository.entities.Lesson> = mutableSetOf()

    @ManyToMany
    @JoinTable(
        joinColumns = [JoinColumn(name = "debrief_id")],
        inverseJoinColumns = [JoinColumn(name = "mission_id")]
    )
    open var missions: MutableSet<org.example.debriefrepository.entities.Mission> = mutableSetOf()

    @ManyToMany
    @JoinTable(
        name = "group_debriefs",
        joinColumns = [JoinColumn(name = "debrief_id")],
        inverseJoinColumns = [JoinColumn(name = "group_id")]
    )
    open var groups: MutableSet<org.example.debriefrepository.entities.Group> = mutableSetOf()

    @ManyToMany
    @JoinTable(
        name = "users_debriefs",
        joinColumns = [JoinColumn(name = "debrief_id")],
        inverseJoinColumns = [JoinColumn(name = "user_id")]
    )
    open var users: MutableSet<org.example.debriefrepository.entities.User> = mutableSetOf()
}