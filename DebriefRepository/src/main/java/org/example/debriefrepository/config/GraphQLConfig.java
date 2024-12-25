package org.example.debriefrepository.config;

import graphql.schema.Coercing;
import graphql.schema.GraphQLScalarType;
import graphql.schema.idl.RuntimeWiring;

import org.example.debriefrepository.resolver.DateResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

@Configuration
public class GraphQLConfig {

    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer()  {
        return builder -> builder.scalar(DateResolver.DATE)
                .build();

//        return RuntimeWiring.newRuntimeWiring()
//                .scalar(DateResolver.DATE) // Register the custom scalar
//                .build();

    }
}
