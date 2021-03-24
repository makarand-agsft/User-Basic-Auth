package com.formz.multitenancy;

import org.hibernate.MultiTenancyStrategy;
import org.hibernate.cfg.Environment;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableWebSecurity
@EnableTransactionManagement
@EnableJpaRepositories("com.formz.repo")
public class HibernateConfig {

    @Autowired
    private JpaProperties jpaProperties;

    @Autowired
    private org.springframework.core.env.Environment env;
    @Bean
    public JpaVendorAdapter jpaVendorAdapter(){
        return new HibernateJpaVendorAdapter();
    }
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource,
            MultiTenantConnectionProvider multiTenantConnectionProvider,
            CurrentTenantIdentifierResolver currentTenantIdentifierResolver){
        Map<String, Object> properties = new HashMap<>();
        properties.putAll(jpaProperties.getProperties());
        properties.put(Environment.MULTI_TENANT, MultiTenancyStrategy.SCHEMA);
        properties.put(Environment.MULTI_TENANT_CONNECTION_PROVIDER, multiTenantConnectionProvider);
        properties.put(Environment.MULTI_TENANT_IDENTIFIER_RESOLVER, currentTenantIdentifierResolver);

        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setDataSource(dataSource);
        entityManagerFactoryBean.setPackagesToScan("com.formz");
        entityManagerFactoryBean.setJpaVendorAdapter(jpaVendorAdapter());
        entityManagerFactoryBean.setJpaPropertyMap(properties);
        return entityManagerFactoryBean;

    }
    @Bean
    public DataSource dataSource(){
        DataSourceBuilder builder = DataSourceBuilder.create();
        builder.driverClassName(env.getProperty("spring.driver.classname"));
        builder.password(env.getProperty("spring.datasource.password"));
        builder.username(env.getProperty("spring.datasource.username"));
        builder.url(env.getProperty("spring.datasource.url"));
        return builder.build();
    }
}
