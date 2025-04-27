package org.example.debriefrepository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
@Embeddable
public class Metadata {

    @Column(name = "created_by", nullable = false)
    private String createdBy;

    @Column(name = "updated_by", nullable = false)
    private String updatedBy;

    @Column(name = "created", nullable = false)
    private ZonedDateTime created;

    @Column(name = "modified", nullable = false)
    private ZonedDateTime modified;
}
