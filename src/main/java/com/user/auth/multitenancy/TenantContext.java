package com.user.auth.multitenancy;

import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.stereotype.Component;


public class TenantContext {


    private static ThreadLocal<String> currentTenant = new ThreadLocal<>();

    static {
        currentTenant.set("master");
    }
    public  static String getCurrentTenant() {
        return currentTenant.get();
    }

    public  static void setCurrentTenant(String tenant) {
        currentTenant.set(tenant);
    }
}
