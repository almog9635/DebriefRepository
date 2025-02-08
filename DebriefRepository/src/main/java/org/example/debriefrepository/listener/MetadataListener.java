package org.example.debriefrepository.listener;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.example.debriefrepository.config.UserContext;
import org.example.debriefrepository.entity.BaseEntity;
import org.example.debriefrepository.entity.Metadata;

import java.time.ZonedDateTime;

public class MetadataListener {

    private static Log log = LogFactory.getLog(MetadataListener.class);

    @PrePersist
    @PreUpdate
    @PreRemove
    private void beforeAnyUpdate(Object baseEntity) {
        String userId = UserContext.getCurrentUserId();
        Metadata metadata = ((BaseEntity) baseEntity).getMetaData();
        if (metadata == null) {
            metadata = new Metadata();
            metadata.setCreated(ZonedDateTime.now());
            metadata.setModified(ZonedDateTime.now());
            metadata.setCreatorId(userId);
            metadata.setModifierId(userId);
            ((BaseEntity) baseEntity).setMetaData(metadata);
        }
        else {
            metadata.setModified(ZonedDateTime.now());
            metadata.setModifierId(userId);
        }
        log.info("[METADATA] " + metadata);
    }
}
