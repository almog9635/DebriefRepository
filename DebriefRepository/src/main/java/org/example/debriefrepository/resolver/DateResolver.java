package org.example.debriefrepository.resolver;

import graphql.schema.Coercing;
import graphql.schema.GraphQLScalarType;

import java.time.Instant;
import java.util.Date;


public class DateResolver {
    public static final GraphQLScalarType DATE = GraphQLScalarType.newScalar()
            .name("Date")
            .description("Date custom scalar type")
            .coercing(new Coercing<Date, Long>() {
                @Override
                public Long serialize(Object dataFetcherResult) {
                    // Convert Java Date to a timestamp (value sent to the client)
                    if (dataFetcherResult instanceof Date) {
                        return ((Date) dataFetcherResult).getTime();
                    }
                    throw new IllegalArgumentException("Invalid type for Date scalar: " + dataFetcherResult);
                }

                @Override
                public Date parseValue(Object input) {
                    // Convert client-provided value to a Java Date
                    if (input instanceof Long) {
                        return new Date((Long) input);
                    }
                    throw new IllegalArgumentException("Invalid value for Date scalar: " + input);
                }

                @Override
                public Date parseLiteral(Object input) {
                    // Parse GraphQL AST literal value into a Java Date
                    if (input instanceof String) {
                        try {
                            return Date.from(Instant.ofEpochMilli(Long.parseLong((String) input)));
                        } catch (NumberFormatException e) {
                            throw new IllegalArgumentException("Invalid literal for Date scalar: " + input, e);
                        }
                    }
                    throw new IllegalArgumentException("Date scalar can only parse literals of type String.");
                }
            })
            .build();
}
