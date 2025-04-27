package org.example.debriefrepository.config.UserContext;

import graphql.GraphQLContext;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class ContextRetriever {

    private static final Map<Long, GraphQLContext> contextMap = new ConcurrentHashMap<>();

    public static void registerContext(GraphQLContext context) {
        contextMap.put(Thread.currentThread().threadId(), context);
    }

    public static GraphQLContext getContext(Long threadId) {
        GraphQLContext context = contextMap.get(threadId);
        if(Objects.isNull(context)) {
            throw new IllegalStateException("you forgot to annotate query/mutation " + threadId);
        }
        return context;
    }

    public static void removeContext(Long threadId) {
        contextMap.remove(threadId);
    }
}