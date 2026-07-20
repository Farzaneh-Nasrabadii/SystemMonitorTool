package com.monitor;

import com.monitor.exception.DatabaseOperationException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Timestamp;

public class DatabaseManager {
    // Read database configurations from environment variables, or use defaults
    private static final String URL = System.getenv("DB_URL") != null ? System.getenv("DB_URL") : "jdbc:postgresql://localhost:5432/system_monitor";
    private static final String USER = System.getenv("DB_USER") != null ? System.getenv("DB_USER") : "postgres";
    private static final String PASSWORD = System.getenv("DB_PASSWORD") != null ? System.getenv("DB_PASSWORD") : "fr001009";

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
        // Updated SQL to include ram_usage
        String sql = "INSERT INTO metrics_history (disk_usage, ram_usage, recorded_at) VALUES (?, ?, ?)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Bind values to the SQL query parameters
            pstmt.setDouble(1, metrics.getDiskUsagePercentage());
            pstmt.setDouble(2, metrics.getRamUsagePercentage());
            pstmt.setTimestamp(3, Timestamp.valueOf(metrics.getTimestamp()));

            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Data successfully saved to PostgreSQL database.");
            }

        } catch (Exception e) {
            // Translate low-level SQL and connection failures into a custom domain exception
            throw new DatabaseOperationException("Failed to save metrics snapshot into PostgreSQL database.", e);
        }
    }
}