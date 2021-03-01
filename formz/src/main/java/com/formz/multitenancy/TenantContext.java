package com.formz.multitenancy;


public class TenantContext {

    final public static String DEFAULT_TENANT = "formz_master";

    private static ThreadLocal<String> currentTenant = new ThreadLocal<String>() {
        @Override
        protected String initialValue() {
            return DEFAULT_TENANT;
        }
    };

    public  static String getCurrentTenant() {
        return currentTenant.get();
    }

    public  static void setCurrentTenant(String tenant) {
        currentTenant.set(tenant);
    }
}
