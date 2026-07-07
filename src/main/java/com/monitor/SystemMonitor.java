package com.monitor;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class SystemMonitor {

    public static void main(String[] args) {
        System.out.println("Starting System Monitor Tool...");

        double currentDiskUsage = getDiskUsagePercentage();
        double currentRamUsage = getRamUsagePercentage();

        if (currentDiskUsage >= 0 && currentRamUsage >= 0) {
            SystemMetrics metrics = new SystemMetrics(currentDiskUsage, currentRamUsage);
            System.out.println("\nSuccessfully collected metrics:");
            System.out.println(metrics);

            // 1. Save to PostgreSQL Database
            DatabaseManager.saveMetrics(metrics);

            // 2. Check Thresholds and Send Alerts (New Part)
            // For testing purposes, you can lower these numbers (e.g., to 10.0) to force an email
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
                // We are looking for the line starting with 'Mem:'
                if (line.startsWith("Mem:")) {
                    // Split the line by spaces to get columns
                    String[] tokens = line.split("\\s+");

                    // In 'free -m' output:
                    // tokens[1] is Total RAM (e.g., 7931)
                    // tokens[2] is Used RAM (e.g., 3250)
                    double totalRam = Double.parseDouble(tokens[1]);
                    double usedRam = Double.parseDouble(tokens[2]);

                    // Calculate percentage: (used / total) * 100
                    usage = (usedRam / totalRam) * 100;

                    // Round to 2 decimal places for clean data
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

    /**
     * Executes the Linux 'df -h' command and parses disk usage.
     */
    public static double getDiskUsagePercentage() {
        String[] command = {"df", "-h"};
        double usage = -1;

        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            reader.readLine(); // Skip header

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