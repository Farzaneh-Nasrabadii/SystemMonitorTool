package com.monitor;

import org.java_websocket.server.WebSocketServer;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import com.monitor.exception.SystemCommandException;
import com.monitor.exception.DatabaseOperationException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import com.monitor.config.ConfigManager;

public class SystemMonitor extends WebSocketServer {

    public SystemMonitor(int port) {
        super(new InetSocketAddress(port));
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("🌐 New frontend client connected: " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("❌ Frontend client disconnected");
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        // No messages expected from frontend clients for now
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        System.err.println("WebSocket Error: " + ex.getMessage());
    }

    @Override
    public void onStart() {
        System.out.println("🚀 WebSocket Server successfully started on port 8080!");
    }

    public static void main(String[] args) {
        int port = ConfigManager.getInt("PORT", 8080);
        SystemMonitor server = new SystemMonitor(port);
        server.start();

        try {
            ProcessBuilder pb = new ProcessBuilder("vmstat", "1");
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            // Skip the first two header lines of vmstat output
            reader.readLine();
            reader.readLine();

            int databaseCounter = 0;

            while ((line = reader.readLine()) != null) {
                try {
                    double currentRamUsage = getRamUsagePercentage();
                    double currentDiskUsage = getDiskUsagePercentage();

                    // 1. Stream real-time data to connected frontend clients via JSON payload
                    String jsonPayload = String.format("{\"ram\": %.2f, \"disk\": %.2f}", currentRamUsage, currentDiskUsage);
                    server.broadcast(jsonPayload);

                    databaseCounter++;

                    // 2. Database Optimization: Log metrics only once every 60 seconds (60 loops)
                    if (databaseCounter >= 60) {
                        System.out.println("💾 [DATABASE LOG] Saving 1-minute interval snapshot to PostgreSQL...");
                        SystemMetrics metrics = new SystemMetrics(currentDiskUsage, currentRamUsage);
                        DatabaseManager.saveMetrics(metrics);
                        databaseCounter = 0;
                    }

                    // 3. Evaluate real-time critical system thresholds for email alerting
                    checkThresholdsAndAlert(currentRamUsage, currentDiskUsage);

                } catch (SystemCommandException e) {
                    // Gracefully catch custom OS command failures to keep the stream alive
                    System.err.println("❌ [OS COMMAND ERROR] " + e.getMessage());
                    if (e.getCause() != null) {
                        System.err.println("➡️ Underlying Cause: " + e.getCause().getMessage());
                    }
                } catch (DatabaseOperationException e) {
                    // Gracefully catch custom database failures without crashing the application loop
                    System.err.println("❌ [DATABASE ERROR] " + e.getMessage());
                    if (e.getCause() != null) {
                        System.err.println("➡️ Underlying Cause: " + e.getCause().getMessage());
                    }
                }
            }
            process.waitFor();
        } catch (Exception e) {
            System.err.println("Streaming error: " + e.getMessage());
        }
    }

    /**
     * Evaluates thresholds and triggers email alerts if necessary.
     * Returns true if an alert was triggered, false otherwise.
     */
    public static boolean checkThresholdsAndAlert(double ram, double disk) {
        if (ram > 85.0 || disk > 90.0) {
            EmailAlertManager.sendEmailAlert("⚠️ CRITICAL SPIKE", "System resource exceeded limits!");
            return true;
        }
        return false;
    }

    /**
     * Executes native OS commands to retrieve current RAM usage percentage.
     * @throws SystemCommandException if execution or parsing fails.
     */
    public static double getRamUsagePercentage() {
        String[] command = {"free", "-m"};
        double usage = -1;
        try {
            Process process = new ProcessBuilder(command).start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Mem:")) {
                    String[] tokens = line.split("\\s+");
                    double totalRam = Double.parseDouble(tokens[1]);
                    double usedRam = Double.parseDouble(tokens[2]);
                    usage = (usedRam / totalRam) * 100;
                    usage = Math.round(usage * 100.0) / 100.0;
                    break;
                }
            }
            process.waitFor();
        } catch (Exception e) {
            // Translate raw low-level exceptions into our custom domain exception
            throw new SystemCommandException("Failed to execute or parse RAM metrics command from the OS environment.", e);
        }
        return usage;
    }

    /**
     * Executes native OS commands to retrieve root directory Disk usage percentage.
     * @throws SystemCommandException if execution or parsing fails.
     */
    public static double getDiskUsagePercentage() {
        String[] command = {"df", "-h"};
        double usage = -1;
        try {
            Process process = new ProcessBuilder(command).start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            reader.readLine(); // Skip the header line
            while ((line = reader.readLine()) != null) {
                if (line.endsWith(" /")) {
                    String[] tokens = line.split("\\s+");
                    usage = Double.parseDouble(tokens[4].replace("%", ""));
                    break;
                }
            }
            process.waitFor();
        } catch (Exception e) {
            // Translate raw low-level exceptions into our custom domain exception
            throw new SystemCommandException("Failed to execute or parse Disk metrics command from the OS environment.", e);
        }
        return usage;
    }
}