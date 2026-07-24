package com.monitor;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SystemMetricsTest {

    @Test
    @DisplayName("Should correctly store and retrieve RAM and Disk usage percentages")
    void testGettersAndConstructor() {
        double expectedDisk = 45.5;
        double expectedRam = 72.3;

        SystemMetrics metrics = new SystemMetrics(expectedDisk, expectedRam);

        assertEquals(expectedDisk, metrics.getDiskUsagePercentage(), "Disk usage should match the constructor value");
        assertEquals(expectedRam, metrics.getRamUsagePercentage(), "RAM usage should match the constructor value");
        assertNotNull(metrics.getTimestamp(), "Timestamp should be automatically initialized");
    }

    @Test
    @DisplayName("toString should contain metric values")
    void testToStringFormat() {
        SystemMetrics metrics = new SystemMetrics(30.0, 60.0);
        String result = metrics.toString();

        assertTrue(result.contains("30.0%"), "toString should present disk percentage formatted");
        assertTrue(result.contains("60.0%"), "toString should present RAM percentage formatted");
    }
}