package org.example.debriefrepository.listener;

import graphql.GraphQLContext;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.example.debriefrepository.config.UserContext.ContextRetriever;
import org.example.debriefrepository.entity.BaseEntity;
import org.example.debriefrepository.entity.Metadata;

import java.time.ZonedDateTime;
import java.util.Objects;

public class MetadataListener {

    private static final Log log = LogFactory.getLog(MetadataListener.class);

    @PreUpdate
    @PrePersist
    private void beforeAnyUpdate(Object baseEntity) {
        GraphQLContext context = ContextRetriever.getContext(Thread.currentThread().threadId());
        String userId =  context.get("userId");
        Metadata metadata = ((BaseEntity) baseEntity).getMetaData();
        if (Objects.isNull(metadata)) {
            metadata = new Metadata();
            metadata.setCreated(ZonedDateTime.now());
            metadata.setModified(ZonedDateTime.now());
            metadata.setCreatedBy(userId);
            metadata.setUpdatedBy(userId);
            ((BaseEntity) baseEntity).setMetaData(metadata);
        } else {
            metadata.setModified(ZonedDateTime.now());
            metadata.setUpdatedBy(userId);
        }
        log.info("[METADATA] " + metadata);
    }
}
