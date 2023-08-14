package com.esmo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class PropertiesController {
    private String configFolder;
    private String configFile;
    private Properties props;

    public PropertiesController() {
        props = new Properties();
        if (System.getProperty("os.name").contains("Windows")) {
            configFolder = System.getProperty("user.home") + "\\AppData\\Local\\esmonotes\\";
        } else {
            configFolder = System.getProperty("user.home") + "/.config/esmonotes/";
        }
        configFile = configFolder + File.separator + "config.properties";
        loadProperties();
    }

    private void loadProperties() {
        try (FileReader fileReader = new FileReader(configFile)) {
            props.load(fileReader);
        } catch (FileNotFoundException e) {
            createPropertiesFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createPropertiesFile() {
        new File(configFolder).mkdir();
        try {
            new File(configFile).createNewFile();
        } catch (IOException e) {
            System.err.println("config file could not be made");
        }

    }

    public void saveProperties() {
        try (FileWriter fileWriter = new FileWriter(configFile);) {
            props.store(fileWriter, "Configurations");
        } catch (IOException e) {
            System.err.println("config file could not be saved");
        }
    }

    public void setProperty(String key, String value) {
        props.setProperty(key, value);
    }

    public String getProperty(String key) {
        return props.getProperty(key);
    }

    public Properties getProperties() {
        return props;
    }

}
