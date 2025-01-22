package org.example.debriefrepository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class BaseEntity {

    @Id
    @Column(name = "id", nullable = false, length = Integer.MAX_VALUE)
    private String id = UUID.randomUUID().toString();

    @Embedded
    private MetaData metaData;


}
