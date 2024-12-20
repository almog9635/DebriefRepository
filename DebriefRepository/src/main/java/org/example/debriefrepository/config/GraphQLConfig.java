package org.example.debriefrepository.config;

import graphql.schema.idl.RuntimeWiring;

import org.example.debriefrepository.resolver.DateResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GraphQLConfig {

    @Bean
    public RuntimeWiring runtimeWiring() {
        return RuntimeWiring.newRuntimeWiring()
                .scalar(DateResolver.DATE) // Register the custom scalar
                .build();
    }
}
