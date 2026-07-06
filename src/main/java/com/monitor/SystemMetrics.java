package com.monitor;

import java.time.LocalDateTime;

public class SystemMetrics {
    private double diskUsagePercentage;
    private double ramUsagePercentage; // New field for RAM
    private LocalDateTime timestamp;

    // Updated Constructor
    public SystemMetrics(double diskUsagePercentage, double ramUsagePercentage) {
        this.diskUsagePercentage = diskUsagePercentage;
        this.ramUsagePercentage = ramUsagePercentage;
        this.timestamp = LocalDateTime.now();
    }

    // Getters
    public double getDiskUsagePercentage() {
        return diskUsagePercentage;
    }

    public double getRamUsagePercentage() {
        return ramUsagePercentage;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "SystemMetrics {" +
                "diskUsagePercentage=" + diskUsagePercentage + "%" +
                ", ramUsagePercentage=" + ramUsagePercentage + "%" +
                ", timestamp=" + timestamp +
                '}';
    }
}