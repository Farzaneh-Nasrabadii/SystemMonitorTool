package com.monitor;

import org.java_websocket.server.WebSocketServer;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import com.monitor.config.ConfigManager;
import com.monitor.exception.SystemCommandException;
import com.monitor.exception.DatabaseOperationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;

public class SystemMonitor extends WebSocketServer {

    private static final Logger logger = LoggerFactory.getLogger(SystemMonitor.class);

    public SystemMonitor(int port) {
        super(new InetSocketAddress(port));
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        logger.info("New frontend client connected: {}", conn.getRemoteSocketAddress());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        logger.warn("Frontend client disconnected: {}", conn.getRemoteSocketAddress());
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        // No messages expected from frontend clients for now
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        logger.error("WebSocket server encountered an error: {}", ex.getMessage(), ex);
    }

    @Override
    public void onStart() {
        logger.info("WebSocket Server successfully started and listening for incoming connections!");
    }

    public static void main(String[] args) {
        int port = ConfigManager.getInt("PORT", 8080);
        SystemMonitor server = new SystemMonitor(port);
        server.start();

        try {
            ProcessBuilder pb = new ProcessBuilder("vmstat", "1");
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            // Skip header lines
            reader.readLine();
            reader.readLine();

            int databaseCounter = 0;

            while ((reader.readLine()) != null) {
                try {
                    double currentRamUsage = getRamUsagePercentage();
                    double currentDiskUsage = getDiskUsagePercentage();

                    String jsonPayload = String.format("{\"ram\": %.2f, \"disk\": %.2f}", currentRamUsage, currentDiskUsage);
                    server.broadcast(jsonPayload);

                    databaseCounter++;

                    if (databaseCounter >= 60) {
                        logger.info("Triggering 1-minute interval snapshot storage to database...");
                        SystemMetrics metrics = new SystemMetrics(currentDiskUsage, currentRamUsage);
                        DatabaseManager.saveMetrics(metrics);
                        databaseCounter = 0;
                    }

                    checkThresholdsAndAlert(currentRamUsage, currentDiskUsage);

                } catch (SystemCommandException e) {
                    logger.error("OS command execution failure in monitoring loop: {}", e.getMessage(), e);
                } catch (DatabaseOperationException e) {
                    logger.error("Database operation failure in monitoring loop: {}", e.getMessage(), e);
                }
            }
            process.waitFor();
        } catch (Exception e) {
            logger.error("Fatal streaming loop error encountered: {}", e.getMessage(), e);
        }
    }

    public static boolean checkThresholdsAndAlert(double ram, double disk) {
        if (ram > 85.0 || disk > 90.0) {
            logger.warn("Resource threshold breach detected! RAM: {}%, Disk: {}%. Triggering email alert...", ram, disk);
            EmailAlertManager.sendEmailAlert("⚠️ CRITICAL SPIKE", "System resource exceeded limits!");
            return true;
        }
        return false;
    }

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
            throw new SystemCommandException("Failed to execute or parse RAM metrics command from the OS environment.", e);
        }
        return usage;
    }

    public static double getDiskUsagePercentage() {
        String[] command = {"df", "-h"};
        double usage = -1;
        try {
            Process process = new ProcessBuilder(command).start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                if (line.endsWith(" /")) {
                    String[] tokens = line.split("\\s+");
                    usage = Double.parseDouble(tokens[4].replace("%", ""));
                    break;
                }
            }
            process.waitFor();
        } catch (Exception e) {
            throw new SystemCommandException("Failed to execute or parse Disk metrics command from the OS environment.", e);
        }
        return usage;
    }
}