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
        config.setJdbcUrl("jdbc:mysql://35.226.70.238:3306/hirun");
        config.setUsername("root");
        config.setPassword("Devrious_45");

        // **Optimized HikariCP Settings**
        config.setMaximumPoolSize(1000);  // Optimal max connections
        config.setMinimumIdle(100);       // Keep at least 50 idle connections
        config.setIdleTimeout(30000);    // Close idle connections after 30s
        config.setMaxLifetime(1800000);  // Recycle connections every 30 min
        config.setConnectionTimeout(5000); // Wait 5s for a free connection

        // Additional Performance Settings
        config.setAutoCommit(true);
        config.setLeakDetectionThreshold(5000); // Detect leaked connections
        config.setValidationTimeout(3000); // Connection validation timeout

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
