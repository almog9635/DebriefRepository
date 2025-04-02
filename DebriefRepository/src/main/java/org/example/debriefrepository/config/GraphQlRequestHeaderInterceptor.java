package org.example.debriefrepository.config;


import org.example.debriefrepository.types.consts.consts;
import org.springframework.graphql.server.WebGraphQlInterceptor;
import org.springframework.graphql.server.WebGraphQlRequest;
import org.springframework.graphql.server.WebGraphQlResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Objects;

@Component
public class GraphQlRequestHeaderInterceptor implements WebGraphQlInterceptor {

    @Override
    public Mono<WebGraphQlResponse> intercept(WebGraphQlRequest request, Chain chain) {
        try {
            String userId = request.getHeaders().getFirst(consts.USER_ID);
            if (Objects.nonNull(userId)) {
                request.configureExecutionInput((executionInput, builder) ->
                        builder.graphQLContext(Collections.singletonMap("userId", userId)).build()
                );
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return chain.next(request);
    }
}