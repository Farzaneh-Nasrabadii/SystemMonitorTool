package com.monitor;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class SystemMonitor {

    public static void main(String[] args) {
        System.out.println("Starting System Monitor Tool...");

        // Execute and parse disk usage
        double currentDiskUsage = getDiskUsagePercentage();

        if (currentDiskUsage >= 0) {
            // Create a metrics object with the parsed data
            SystemMetrics metrics = new SystemMetrics(currentDiskUsage);
            System.out.println("\nSuccessfully collected metrics:");
            System.out.println(metrics);

            // Save the collected metrics to the database
            DatabaseManager.saveMetrics(metrics);
        } else {
            System.err.println("Failed to collect system metrics.");
        }
    }

    public static double getDiskUsagePercentage() {
        String[] command = {"df", "-h"};
        double usage = -1; // -1 indicates an error or not found

        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            // Skip the header line of 'df -h'
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                // We are looking for the root filesystem line (ends with space and /)
                if (line.endsWith(" /")) {
                    // Split the line by spaces to isolate columns
                    String[] tokens = line.split("\\s+");

                    // In 'df -h', the Use% column is typically the 5th column (index 4)
                    // Example token: "45%"
                    String usePercentageStr = tokens[4].replace("%", "");

                    // Convert the string to a double
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