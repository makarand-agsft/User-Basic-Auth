package com.user.auth.multitenancy;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.stereotype.Component;

@Component
public class CurrentTenantIdentifierResolverImpl implements CurrentTenantIdentifierResolver {

    final  public String DEFAULT_TENANT = "master";

    @Override public String resolveCurrentTenantIdentifier() {
        String tenant = TenantContext.getCurrentTenant();
        if (tenant != null)
            return tenant;
        return DEFAULT_TENANT;
    }

    @Override public boolean validateExistingCurrentSessions() {
        return true;
    }
}
