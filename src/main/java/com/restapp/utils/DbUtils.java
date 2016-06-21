package com.restapp.utils;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DbUtils {

    public static void createTables(Connection connection) throws SQLException {
        try {
            Statement statement = connection.createStatement();
            statement
                    .executeUpdate("create table if not exists bird (name string primary key, color string, height double, weight double)");
            statement
                    .executeUpdate("create table if not exists sighting (id integer primary key autoincrement,name string, location string, date string, foreign key(name) references bird(name))");
        } catch (Exception ex) {
            throw ex;
        } finally {
            connection.close();
        }

    }

    public static void deleteTables(Connection connection) throws SQLException {
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("delete from bird");
            statement.executeUpdate("delete from sighting");
        } catch (Exception ex) {
            throw ex;
        } finally {
            connection.close();
        }
    }
}
