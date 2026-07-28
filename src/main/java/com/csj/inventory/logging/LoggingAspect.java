package com.csj.inventory.logging;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private final StructuredLogger structuredLogger;

    @Value("${app.logging.slow-request-threshold-ms:500}")
    private long slowThresholdMs;

    public LoggingAspect(StructuredLogger structuredLogger) {
        this.structuredLogger = structuredLogger;
    }

    @Around("@annotation(loggable)")
    public Object logAround(ProceedingJoinPoint joinPoint, Loggable loggable) throws Throwable {
        long start = System.currentTimeMillis();
        String user = currentUsername();
        String status = "SUCCESS";
        Object result = null;
        try {
            result = joinPoint.proceed();
            return result;
        } catch (Throwable ex) {
            status = "FAILURE";
            throw ex;
        } finally {
            long elapsed = System.currentTimeMillis() - start;
            String entityId = extractEntityId(result, joinPoint.getArgs());
            structuredLogger.logEvent(user, loggable.action(), loggable.entity(), entityId, status, elapsed);
            if (elapsed > slowThresholdMs) {
                structuredLogger.logSlowRequest(user, loggable.action(), elapsed, slowThresholdMs);
            }
        }
    }

    private String currentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth != null && auth.isAuthenticated()) ? auth.getName() : "anonymous";
    }

    private String extractEntityId(Object result, Object[] args) {
        try {
            if (result != null) {
                var idMethod = result.getClass().getMethod("getId");
                Object id = idMethod.invoke(result);
                if (id != null) return id.toString();
            }
        } catch (Exception ignored) {
            // result has no getId() accessor - fall back to args
        }
        if (args != null && args.length > 0 && args[0] != null) {
            return args[0].toString();
        }
        return "n/a";
    }
}
