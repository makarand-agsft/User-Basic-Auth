package com.formz.multitenancy;

import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
public class MultiTenantDataSourceConfig extends AbstractDataSourceBasedMultiTenantConnectionProviderImpl {

    @Autowired private DataSource dataSource;


    private String DEFAULT_TENANT = "formz_master";


    @Override protected DataSource selectAnyDataSource() {
        return dataSource;
    }

    @Override protected DataSource selectDataSource(String tenantIdentifier) {
        return null;
    }

    @Override public Connection getAnyConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override public void releaseAnyConnection(Connection connection) throws SQLException {
        connection.close();

    }


    @Override public Connection getConnection(String tenantIdentifier) throws SQLException {
        tenantIdentifier = TenantContext.getCurrentTenant();
        Connection connection = getAnyConnection();
        try {
            if (connection != null) {
                if (tenantIdentifier != null) {
                    connection.createStatement().execute("USE " + tenantIdentifier);
                } else {
                    connection.createStatement().execute("USE " + DEFAULT_TENANT);

                }

            }
        } catch (SQLException exception) {
            exception.printStackTrace();
            throw new SQLException("Error in db connection");
        }

        return connection;
    }

    @Override public void releaseConnection(String tenantIdentifier, Connection connection) throws SQLException {
        connection.close();
    }

    @Override public boolean supportsAggressiveRelease() {
        return true;
    }

    @Override public boolean isUnwrappableAs(Class unwrapType) {
        return false;
    }

    @Override public <T> T unwrap(Class<T> unwrapType) {
        return null;
    }
}
