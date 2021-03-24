package com.formz.config;

import com.formz.multitenancy.TenantContext;
import org.springframework.core.task.TaskDecorator;

public class TenantAwareTaskExecutor implements TaskDecorator {

        @Override
        public Runnable decorate(Runnable runnable) {
            String tenantName = TenantContext.getCurrentTenant();
            return () -> {
                try {
                    TenantContext.setCurrentTenant(tenantName);
                    runnable.run();
                } finally {
                    TenantContext.setCurrentTenant(null);
                }
            };
        }
    }
