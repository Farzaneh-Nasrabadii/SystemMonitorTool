package com.monitor.config;

import com.monitor.config.ConfigManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConfigManagerTest {

    @Test
    @DisplayName("Should return default string value when key does not exist")
    void testGetDefaultValueForMissingKey() {
        String result = ConfigManager.get("NON_EXISTENT_KEY_12345", "default_value");
        assertEquals("default_value", result, "Expected default value when key is missing");
    }

    @Test
    @DisplayName("Should return default integer value when key does not exist")
    void testGetIntDefaultValueForMissingKey() {
        int result = ConfigManager.getInt("NON_EXISTENT_PORT_12345", 9090);
        assertEquals(9090, result, "Expected default integer value when key is missing");
    }
}