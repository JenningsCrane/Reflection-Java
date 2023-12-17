package edu.school21.DataBaseLoader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public final class Application {
    private static final Properties PROPERTIES = new Properties();
    private Application(){}
    static {
        try {
            loadProperties();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    private static void loadProperties() throws FileNotFoundException {
        try (FileInputStream inputStream = new FileInputStream("src/main/resources/applications.properties")) {
            PROPERTIES.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String get(String key) {
        return PROPERTIES.getProperty(key);
    }
}

