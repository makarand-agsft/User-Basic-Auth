package com.user.auth.multitenancy;

import org.springframework.stereotype.Component;

@Component
public class TenantContext {

    private static ThreadLocal<String> currentTenant = new ThreadLocal<>();

    public  static String getCurrentTenant() {
        return currentTenant.get();
    }

    public  static void setCurrentTenant(String tenant) {
        currentTenant.set(tenant);
    }
}
