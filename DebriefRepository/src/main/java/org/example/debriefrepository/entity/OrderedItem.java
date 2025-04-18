package org.example.debriefrepository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public class OrderedItem extends BaseEntity {

    @Column(name = "index", nullable = false)
    private Long index;
}
