package com.hirun.gemora;

import java.sql.Connection;
import java.sql.SQLException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DatabaseConnection {
    private static final HikariConfig config = new HikariConfig();
    private static final HikariDataSource ds;

    static {
        // Database configuration
        config.setJdbcUrl("jdbc:mysql://localhost:3306/hirun");
        config.setUsername("root");
        config.setPassword("savindu");

        ds = new HikariDataSource(config);

        // Register a shutdown hook to close the connection pool
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try (ds) {
                System.out.println("Shutting down database connection pool...");
            }
        }));
    }

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }
}
