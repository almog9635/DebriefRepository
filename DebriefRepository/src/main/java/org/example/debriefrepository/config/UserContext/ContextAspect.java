package org.example.debriefrepository.config.UserContext;

import graphql.schema.DataFetchingEnvironment;
import lombok.Setter;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ContextAspect {

//    @Setter(onMethod = @__(@Autowired))
//    private ContextRetriever contextRetriever;

    @Around("@annotation(WithUserContext) && args(.., environment)")
    public Object manageUserContext(ProceedingJoinPoint joinPoint, DataFetchingEnvironment environment) throws Throwable {
         ContextRetriever.registerContext(environment.getGraphQlContext());
        
        return joinPoint.proceed();
    }
}