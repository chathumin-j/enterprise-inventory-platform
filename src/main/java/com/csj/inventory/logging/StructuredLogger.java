package com.csj.inventory.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Component
public class StructuredLogger {

    private static final Logger log = LoggerFactory.getLogger("com.csj.inventory.audit");

    public void logEvent(String user, String action, String entity, String entityId, String status, Long executionTimeMs) {
        try {
            MDC.put("user", safe(user));
            MDC.put("action", safe(action));
            MDC.put("entity", safe(entity));
            MDC.put("entityId", safe(entityId));
            MDC.put("status", safe(status));
            if (executionTimeMs != null) {
                MDC.put("executionTime", executionTimeMs + "ms");
            }
            log.info("{} {} on {} [{}] -> {}", user, action, entity, entityId, status);
        } finally {
            MDC.clear();
        }
    }

    public void logLoginAttempt(String username, boolean success, Long executionTimeMs) {
        logEvent(username, "LOGIN", "User", username, success ? "SUCCESS" : "FAILURE", executionTimeMs);
    }

    public void logSlowRequest(String user, String action, long executionTimeMs, long thresholdMs) {
        try {
            MDC.put("user", safe(user));
            MDC.put("action", safe(action));
            MDC.put("status", "SLOW");
            MDC.put("executionTime", executionTimeMs + "ms");
            log.warn("SLOW_REQUEST {} took {}ms (threshold {}ms)", action, executionTimeMs, thresholdMs);
        } finally {
            MDC.clear();
        }
    }

    public void logException(String action, Exception ex, String path) {
        try {
            MDC.put("action", safe(action));
            MDC.put("entity", safe(path));
            MDC.put("status", "ERROR");
            log.error("EXCEPTION during {} at {} - {}", action, path, ex.getMessage(), ex);
        } finally {
            MDC.clear();
        }
    }

    private String safe(String value) {
        return value == null ? "unknown" : value;
    }
}
