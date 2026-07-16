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
}