package org.example.debriefrepository.config;

import org.example.debriefrepository.resolver.DateResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

@Configuration
public class GraphQLConfig {

    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer() {
        return builder -> builder
                .scalar(DateResolver.DATE)
                .scalar(DateResolver.ZONED_DATE_TIME)
                .build();
    }

}
