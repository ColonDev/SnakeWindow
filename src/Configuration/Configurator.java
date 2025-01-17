package Configuration;

import java.io.*;
import java.util.Properties;

public class Configurator {
    private Properties properties;
    private String configFilePath;

    public Configurator(String configFilePath) {
        this.configFilePath = configFilePath;
        properties = new Properties();
        loadProperties();
    }

    private void loadProperties() {
        try (InputStream input = new FileInputStream(configFilePath)) {
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public void setProperty(String key, String value) {
        properties.setProperty(key, value);
    }

    public void saveProperties() {
        try (OutputStream output = new FileOutputStream(configFilePath)) {
            properties.store(output, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Properties getAllProperties() {
        return properties;
    }
}