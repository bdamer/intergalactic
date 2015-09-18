package com.afqa123.intergalactic.asset;

import java.util.Properties;

public class Strings {

    private static Properties properties;
    
    public static void initialize(Properties properties) {
        Strings.properties = properties;
    }
    
    public static String get(String id) {
        return properties.getProperty(id);
    }
}
