package com.monitor;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class SystemMonitor {

    public static void main(String[] args) {
        System.out.println("Starting System Monitor Tool...");

        // Step 1: Get Disk Usage
        double currentDiskUsage = getDiskUsagePercentage();

        // Step 2: Get RAM Usage (New Part)
        double currentRamUsage = getRamUsagePercentage();

        // Check if both metrics were collected successfully
        if (currentDiskUsage >= 0 && currentRamUsage >= 0) {
            // Step 3: Create a metrics object with both values
            SystemMetrics metrics = new SystemMetrics(currentDiskUsage, currentRamUsage);
            System.out.println("\nSuccessfully collected metrics:");
            System.out.println(metrics);

            // Step 4: Save everything to PostgreSQL
            DatabaseManager.saveMetrics(metrics);
        } else {
            System.err.println("Failed to collect system metrics.");
        }
    }

    /**
     * Executes the Linux 'free -m' command and parses the output
     * to calculate the RAM usage percentage.
     */
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