package com.monitor;

import com.monitor.config.ConfigManager;
import com.monitor.exception.DatabaseOperationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Timestamp;

public class DatabaseManager {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseManager.class);

    private static final String URL = ConfigManager.get("DB_URL", "jdbc:postgresql://localhost:5432/system_monitor");
    private static final String USER = ConfigManager.get("DB_USER", "postgres");
    private static final String PASSWORD = ConfigManager.get("DB_PASSWORD", "fr001009");

    private static Connection connect() throws Exception {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void saveMetrics(SystemMetrics metrics) {
        String sql = "INSERT INTO metrics_history (disk_usage, ram_usage, recorded_at) VALUES (?, ?, ?)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, metrics.getDiskUsagePercentage());
            pstmt.setDouble(2, metrics.getRamUsagePercentage());
            pstmt.setTimestamp(3, Timestamp.valueOf(metrics.getTimestamp()));

            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                logger.info("Successfully persisted system metrics snapshot to PostgreSQL database.");
            }

        } catch (Exception e) {
            logger.error("Failed to execute database insert for system metrics: {}", e.getMessage());
            throw new DatabaseOperationException("Failed to save metrics snapshot into PostgreSQL database.", e);
        }
    }
}