package com.monitor;

import com.monitor.config.ConfigManager;
import com.monitor.exception.DatabaseOperationException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Timestamp;

public class DatabaseManager {
    // Read configuration values dynamically via ConfigManager (.env or environment variables)
    private static final String URL = ConfigManager.get("DB_URL", "jdbc:postgresql://localhost:5432/system_monitor");
    private static final String USER = ConfigManager.get("DB_USER", "postgres");
    private static final String PASSWORD = ConfigManager.get("DB_PASSWORD", "fr001009");

    /**
     * Establishes a connection to the PostgreSQL database.
     */
    private static Connection connect() throws Exception {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * Inserts a new system metrics record into the database.
     * @throws DatabaseOperationException if connection or SQL execution fails.
     */
    public static void saveMetrics(SystemMetrics metrics) {
        String sql = "INSERT INTO metrics_history (disk_usage, ram_usage, recorded_at) VALUES (?, ?, ?)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, metrics.getDiskUsagePercentage());
            pstmt.setDouble(2, metrics.getRamUsagePercentage());
            pstmt.setTimestamp(3, Timestamp.valueOf(metrics.getTimestamp()));

            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Data successfully saved to PostgreSQL database.");
            }

        } catch (Exception e) {
            throw new DatabaseOperationException("Failed to save metrics snapshot into PostgreSQL database.", e);
        }
    }
}