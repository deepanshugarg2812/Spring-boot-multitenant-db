package com.main.config;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.slf4j.MDC;

@Slf4j
public class ConfigCurrentTenantIdentifierResolver implements CurrentTenantIdentifierResolver {
    @Override
    public String resolveCurrentTenantIdentifier() {
        log.info("Request came with {}", MDC.get("type"));
        if ("master".equalsIgnoreCase(MDC.get("type"))) {
            return "master";
        } return "slave";
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}
