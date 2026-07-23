package com.monitor;

import com.monitor.SystemMonitor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SystemMonitorTest {

    @Test
    @DisplayName("Should not trigger alert when resources are within healthy limits")
    void testCheckThresholdsNormalUsage() {
        boolean alertTriggered = SystemMonitor.checkThresholdsAndAlert(50.0, 60.0);
        assertFalse(alertTriggered, "Alert should not be triggered for normal resource usage");
    }

    @Test
    @DisplayName("Should trigger alert when RAM usage exceeds threshold (85%)")
    void testCheckThresholdsHighRamSpike() {
        boolean alertTriggered = SystemMonitor.checkThresholdsAndAlert(88.5, 40.0);
        assertTrue(alertTriggered, "Alert should be triggered when RAM exceeds 85%");
    }

    @Test
    @DisplayName("Should trigger alert when Disk usage exceeds threshold (90%)")
    void testCheckThresholdsHighDiskSpike() {
        boolean alertTriggered = SystemMonitor.checkThresholdsAndAlert(30.0, 92.1);
        assertTrue(alertTriggered, "Alert should be triggered when Disk exceeds 90%");
    }
}