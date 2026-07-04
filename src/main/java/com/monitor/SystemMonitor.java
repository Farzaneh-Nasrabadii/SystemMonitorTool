package com.monitor;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class SystemMonitor {

    public static void main(String[] args) {
        System.out.println("Starting System Monitor Tool...");

        // Execute the Linux disk usage command
        checkDiskUsage();
    }

    public static void checkDiskUsage() {
        // 'df -h' is the Linux command to check free disk space
        String[] command = {"df", "-h"};

        try {
            // ProcessBuilder is used to create and start operating system processes
            ProcessBuilder processBuilder = new ProcessBuilder(command);

            // Start the process
            Process process = processBuilder.start();

            // Read the output from the command execution
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            System.out.println("\n--- Linux 'df -h' Output ---");
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            // Wait for the process to finish and get the exit code (0 means success)
            int exitCode = process.waitFor();
            System.out.println("----------------------------");
            System.out.println("Process finished with exit code: " + exitCode);

        } catch (Exception e) {
            System.err.println("Error executing Linux command: " + e.getMessage());
            e.printStackTrace();
        }
    }
}