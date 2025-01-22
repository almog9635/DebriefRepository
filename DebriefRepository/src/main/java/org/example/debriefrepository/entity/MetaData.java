package org.example.debriefrepository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.UUID;

@Embeddable
@Getter
@Setter
public class MetaData {

    private ZonedDateTime created;

    private ZonedDateTime modified;

    private String creatorId;

    private String modifierId;
}
