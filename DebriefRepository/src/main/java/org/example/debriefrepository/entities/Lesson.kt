package org.example.debriefrepository.entities

import jakarta.persistence.*

@Entity
@Table(name = "lesson", schema = "debrief_mgmt")
open class Lesson {
    @Id
    @Column(name = "id", nullable = false)
    open var id: Long? = null

    @Column(name = "content", nullable = false, length = Integer.MAX_VALUE)
    open var content: String? = null

    @ManyToMany(mappedBy = "lessons")
    open var debriefs: MutableSet<Debrief> = mutableSetOf()

    @ManyToMany(mappedBy = "lessons")
    open var missions: MutableSet<org.example.debriefrepository.entities.Mission> = mutableSetOf()
}