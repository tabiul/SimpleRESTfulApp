package com.restapp.main;

import java.beans.PropertyVetoException;

import javax.sql.DataSource;

import com.google.inject.Provider;
import com.mchange.v2.c3p0.ComboPooledDataSource;

public class DatabaseProvider implements Provider<DataSource> {

    private DataSource dataSource;

    private void createConnectionPool() {
        if (dataSource == null) {
            try {
                ComboPooledDataSource cpds = new ComboPooledDataSource();
                cpds.setDriverClass("org.sqlite.JDBC");
                cpds.setJdbcUrl("jdbc:sqlite:memory");
                dataSource = cpds;
            } catch (PropertyVetoException e) {
                e.printStackTrace();
                dataSource = null;
            }
        }

    }

    public DataSource get() {
        createConnectionPool();
        if (dataSource != null) {
            return dataSource;
        }
        return null;
    }
}
