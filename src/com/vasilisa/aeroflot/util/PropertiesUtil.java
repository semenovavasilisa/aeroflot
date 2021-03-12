package com.vasilisa.aeroflot.util;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.util.Properties;

@UtilityClass
public class PropertiesUtil {
    private static final Properties PROPERTIES = new Properties();

    static {
        loadProperties();
    }

    public static String get(String key) {
        return PROPERTIES.getProperty(key);
    }

    @SneakyThrows
    private static void loadProperties() {
        try (var inputStream = PropertiesUtil.class.getClassLoader().getResourceAsStream("application.properties")) {
            PROPERTIES.load(inputStream);
        }
    }
}
