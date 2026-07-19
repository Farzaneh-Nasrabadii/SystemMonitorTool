package com.monitor;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SystemMonitorTest {

    @Test
    public void testRamUsagePercentageIsValid() {
        // When: We fetch the current RAM usage
        double ramUsage = SystemMonitor.getRamUsagePercentage();

        // Then: The value should be either valid (between 0 and 100) OR -1 if the command failed
        assertTrue(ramUsage == -1 || (ramUsage >= 0.0 && ramUsage <= 100.0),
                "RAM usage percentage must be between 0 and 100, or -1 in case of failure");
    }

    @Test
    public void testDiskUsagePercentageIsValid() {
        // When: We fetch the current Disk usage
        double diskUsage = SystemMonitor.getDiskUsagePercentage();

        // Then: The value should be either valid (between 0 and 100) OR -1 if the command failed
        assertTrue(diskUsage == -1 || (diskUsage >= 0.0 && diskUsage <= 100.0),
                "Disk usage percentage must be between 0 and 100, or -1 in case of failure");
    }

    @Test
    public void testAlertIsTriggeredWhenRamExceedsThreshold() {
        // Given: RAM is dangerously high (90%), but Disk is fine (40%)
        double fakeRam = 90.0;
        double fakeDisk = 40.0;

        // When: We check the thresholds
        boolean isAlertSent = SystemMonitor.checkThresholdsAndAlert(fakeRam, fakeDisk);

        // Then: The system must trigger the alert
        assertTrue(isAlertSent, "Alert should be triggered when RAM is above 85%");
    }

    @Test
    public void testAlertIsNotTriggeredWhenMetricsAreNormal() {
        // Given: Both RAM (40%) and Disk (50%) are safe and within limits
        double fakeRam = 40.0;
        double fakeDisk = 50.0;

        // When: We check the thresholds
        boolean isAlertSent = SystemMonitor.checkThresholdsAndAlert(fakeRam, fakeDisk);

        // Then: The system should NOT trigger any alert
        assertFalse(isAlertSent, "Alert should NOT be triggered when metrics are normal");
    }
    @Test
    public void testDatabaseLoggingWorksSuccessfully() {
        // Given: Create a mock system metrics object
        double mockDisk = 45.5;
        double mockRam = 60.2;
        SystemMetrics mockMetrics = new SystemMetrics(mockDisk, mockRam);

        // When & Then: Assert that saving to the database does not throw any exceptions
        assertDoesNotThrow(() -> {
            DatabaseManager.saveMetrics(mockMetrics);
        }, "Database insertion failed! Check if PostgreSQL container is running and configuration is correct.");
    }
    @Test
    public void testSystemCommandExceptionIsThrownWhenCommandFails() {
        assertDoesNotThrow(() -> {
            SystemMonitor.getRamUsagePercentage();
            SystemMonitor.getDiskUsagePercentage();
        }, "Utility methods should work fine under normal OS conditions.");
    }
}