package com.monitor;

import java.time.LocalDateTime;

public class SystemMetrics {
    private double diskUsagePercentage;
    private LocalDateTime timestamp;

    // Constructor
    public SystemMetrics(double diskUsagePercentage) {
        this.diskUsagePercentage = diskUsagePercentage;
        this.timestamp = LocalDateTime.now(); // Captures the exact time of collection
    }

    // Getters
    public double getDiskUsagePercentage() {
        return diskUsagePercentage;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    // Overriding toString() for beautiful logging
    @Override
    public String toString() {
        return "SystemMetrics {" +
                "diskUsagePercentage=" + diskUsagePercentage + "%" +
                ", timestamp=" + timestamp +
                '}';
    }
}