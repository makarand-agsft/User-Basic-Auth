package com.palminsurance.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
public class GeneralConfig {

    public DataSource getDataSource(){
            DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
            driverManagerDataSource.setUrl("jdbc:mysql://localhost:3306/pi_client");
            driverManagerDataSource.setUsername("root");
            driverManagerDataSource.setPassword("root");
            driverManagerDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
            return driverManagerDataSource;
        }

    @Bean
    public SimpleCORSFilter simpleCORSFilterBean() throws Exception {
        return new SimpleCORSFilter();
    }
    }

