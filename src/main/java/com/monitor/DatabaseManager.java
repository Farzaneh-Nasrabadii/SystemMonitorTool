package com.monitor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Timestamp;

public class DatabaseManager {
    // Database connection credentials
    private static final String URL = "jdbc:postgresql://localhost:5432/system_monitor";
    private static final String USER = "postgres";
    private static final String PASSWORD = "fr001009"; // Replace with your actual PostgreSQL password

    /**
     * Establishes a connection to the PostgreSQL database.
     */
    private static Connection connect() throws Exception {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * Inserts a new system metrics record into the database.
     */
    public static void saveMetrics(SystemMetrics metrics) {
        // Updated SQL to include ram_usage
        String sql = "INSERT INTO metrics_history (disk_usage, ram_usage, recorded_at) VALUES (?, ?, ?)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Bind values to the SQL query parameters
            pstmt.setDouble(1, metrics.getDiskUsagePercentage());
            pstmt.setDouble(2, metrics.getRamUsagePercentage()); // New parameter
            pstmt.setTimestamp(3, Timestamp.valueOf(metrics.getTimestamp()));

            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Data successfully saved to PostgreSQL database.");
            }

        } catch (Exception e) {
            System.err.println("Database error while saving metrics: " + e.getMessage());
            e.printStackTrace();
        }
    }
}