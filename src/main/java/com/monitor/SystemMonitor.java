package com.monitor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SystemMonitor {

    public static void main(String[] args) {
        System.out.println("Starting System Monitor Tool...");

        // Create a scheduler thread pool with 1 thread
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        // Define the task we want to run repeatedly
        Runnable monitorTask = new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("\n--- Running Automated Health Check ---");

                    // 1. Collect Metrics
                    double currentDiskUsage = getDiskUsagePercentage();
                    double currentRamUsage = getRamUsagePercentage();

                    if (currentDiskUsage >= 0 && currentRamUsage >= 0) {
                        SystemMetrics metrics = new SystemMetrics(currentDiskUsage, currentRamUsage);
                        System.out.println("Collected metrics: " + metrics);

                        // 2. Save to PostgreSQL
                        DatabaseManager.saveMetrics(metrics);

                        // 3. Check Thresholds (Set to 80% or lower for testing)
                        double DISK_THRESHOLD = 80.0;
                        double RAM_THRESHOLD = 80.0;

                        if (currentDiskUsage > DISK_THRESHOLD || currentRamUsage > RAM_THRESHOLD) {
                            String emailSubject = "⚠️ CRITICAL: Server Resource Alert!";
                            String emailBody = "Attention Admin,\n\n" +
                                    "Your server resources have exceeded the allowed limit:\n" +
                                    "- Current Disk Usage: " + currentDiskUsage + "%\n" +
                                    "- Current RAM Usage: " + currentRamUsage + "%\n\n" +
                                    "Please check the server immediately.";

                            EmailAlertManager.sendEmailAlert(emailSubject, emailBody);
                        } else {
                            System.out.println("System health is OK. No alerts needed.");
                        }

                    } else {
                        System.err.println("Failed to collect system metrics.");
                    }
                } catch (Exception e) {
                    System.err.println("An error occurred during execution loop: " + e.getMessage());
                }
            }
        };

        // Schedule the task to run every 10 seconds (for testing purposes)
        // In real production, you might change this to 5 or 10 minutes.
        long initialDelay = 0;
        long period = 10;

        scheduler.scheduleAtFixedRate(monitorTask, initialDelay, period, TimeUnit.SECONDS);

        System.out.println("Monitor task scheduled to run every " + period + " seconds.");
    }

    public static double getRamUsagePercentage() {
        String[] command = {"free", "-m"};
        double usage = -1;
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            Process process = processBuilder.start();
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
            System.err.println("Error parsing RAM usage: " + e.getMessage());
        }
        return usage;
    }

    public static double getDiskUsagePercentage() {
        String[] command = {"df", "-h"};
        double usage = -1;
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                if (line.endsWith(" /")) {
                    String[] tokens = line.split("\\s+");
                    String usePercentageStr = tokens[4].replace("%", "");
                    usage = Double.parseDouble(usePercentageStr);
                    break;
                }
            }
            process.waitFor();
        } catch (Exception e) {
            System.err.println("Error parsing disk usage: " + e.getMessage());
        }
        return usage;
    }
}