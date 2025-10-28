package com.spribe.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Configuration manager for reading and providing application configuration
 */
public class ConfigManager {
    private static final Logger logger = LogManager.getLogger(ConfigManager.class);
    private static final String CONFIG_FILE = "config.properties";
    private static ConfigManager instance;
    private Properties properties;

    private ConfigManager() {
        loadProperties();
    }

    public static synchronized ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }

    private void loadProperties() {
        properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                logger.error("Unable to find {}", CONFIG_FILE);
                throw new RuntimeException("Configuration file not found: " + CONFIG_FILE);
            }
            properties.load(input);
            logger.info("Configuration loaded successfully from {}", CONFIG_FILE);
        } catch (IOException e) {
            logger.error("Error loading configuration file", e);
            throw new RuntimeException("Error loading configuration file", e);
        }
    }

    public String getBaseUrl() {
        return getProperty("base.url");
    }

    public String getSwaggerPath() {
        return getProperty("swagger.path");
    }

    public int getThreadCount() {
        String threadCount = getProperty("thread.count");
        return Integer.parseInt(threadCount);
    }

    public int getTimeoutSeconds() {
        String timeout = getProperty("timeout.seconds");
        return Integer.parseInt(timeout);
    }

    public String getSupervisorLogin() {
        return getProperty("supervisor.login");
    }

    public String getAdminLogin() {
        return getProperty("admin.login");
    }

    public String getProperty(String key) {
        String value = System.getProperty(key);
        if (value == null) {
            value = properties.getProperty(key);
        }
        if (value == null) {
            logger.warn("Property {} not found", key);
        }
        return value;
    }

    public String getProperty(String key, String defaultValue) {
        String value = getProperty(key);
        return value != null ? value : defaultValue;
    }
}
