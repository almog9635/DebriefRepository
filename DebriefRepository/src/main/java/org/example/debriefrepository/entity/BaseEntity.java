package org.example.debriefrepository.entity;

import jakarta.persistence.*;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;
import org.example.debriefrepository.listener.MetadataListener;

import java.util.UUID;

@MappedSuperclass
@EntityListeners(MetadataListener.class)
@Getter
@Setter
public class BaseEntity {

    @Id
    @Column(name = "id", nullable = false)
    private String id = UUID.randomUUID().toString();

    @Embedded
    private Metadata metaData;


}