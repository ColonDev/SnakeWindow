package Utilities;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class CustomConfig {
    private static CustomConfig instance;
    private final Properties properties;

    private CustomConfig(String configFilePath) {
        properties = new Properties();
        try (FileInputStream fis = new FileInputStream(configFilePath)) {
            properties.load(fis);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static CustomConfig getInstance(String configFilePath) {
        if (instance == null) {
            instance = new CustomConfig(configFilePath);
        }
        return instance;
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public int getIntProperty(String key) {
        return Integer.parseInt(properties.getProperty(key));
    }

    public int getIntProperty(String key, int defaultValue) {
        return Integer.parseInt(properties.getProperty(key, String.valueOf(defaultValue)));
    }
}