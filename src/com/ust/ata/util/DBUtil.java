package com.ust.ata.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {

    // Database Credentials (MySQL)
    // NOTE: Configure these to match your local database setup
    private static final String URL = "jdbc:mysql://localhost:3306/ata_db";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "password_2012"; 

    /**
     * Establishes a connection to the database.
     * Reference: DD Page 13 - static Connection getDBConnection(String driverType)
     * * @param driverType The type of driver (e.g., "mysql", "oracle")
     * @return Connection object or null if connection fails
     */
    public static Connection getDBConnection(String driverType) {
        Connection con = null;
        try {
            if (driverType.equalsIgnoreCase("mysql")) {
                // Load MySQL Driver
                Class.forName("com.mysql.cj.jdbc.Driver");
                con = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            } else if (driverType.equalsIgnoreCase("oracle")) {
                // Placeholder for Oracle (as mentioned in DD environment specs)
                Class.forName("oracle.jdbc.driver.OracleDriver");
                con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "system", "manager");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Driver Class Not Found: " + e.getMessage());
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Database Connection Failed: " + e.getMessage());
            e.printStackTrace();
        }
        return con;
    }
}