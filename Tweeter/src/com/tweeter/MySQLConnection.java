package com.twitter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLConnection {
    static final String URL = "jdbc:mysql://localhost:3306/mydatabase";
    static final String USER = "root";
    static final String PASS = "sqlpassword";

    public static Connection getConnection() {
        Connection conn = null;

        try {
            conn = DriverManager.getConnection(URL, USER, PASS);
        } catch ( SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }
}