package org.example.debriefrepository.config.UserContext;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class UserContextAspect {

    @Around("@annotation(WithUserContext) && args(.., userId)")
    public Object manageUserContext(ProceedingJoinPoint joinPoint, String userId) throws Throwable {
        try {
            UserContext.setCurrentUserId(userId);
            return joinPoint.proceed();
        } finally {
            UserContext.clear();
        }
    }
}