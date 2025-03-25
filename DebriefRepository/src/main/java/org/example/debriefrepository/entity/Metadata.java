package org.example.debriefrepository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Embeddable
@Getter
@Setter
public class Metadata {

    @Column(name = "creator_id", nullable = false)
    private String creatorId;

    @Column(name = "modifier_id", nullable = false)
    private String modifierId;

    @Column(name = "created", nullable = false)
    private ZonedDateTime created;

    @Column(name = "modified", nullable = false)
    private ZonedDateTime modified;
}
