package com.healthcare.readmission.config;

import java.io.InputStream;
import java.util.Properties;

/**
 * Application configuration loader
 * Reads properties from application.properties
 */
public class AppConfig {
    
    private static final Properties properties = new Properties();
    
    static {
        try {
            InputStream input = AppConfig.class.getClassLoader()
                    .getResourceAsStream("application.properties");
            if (input == null) {
                throw new RuntimeException("Unable to find application.properties");
            }
            properties.load(input);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load application.properties", e);
        }
    }
    
    // Application name
    public static String getAppName() {
        return properties.getProperty("app.name", "Patient Readmission Prediction");
    }
    
    // Data paths
    public static String getRawDataPath() {
        return properties.getProperty("data.raw.path", "data/raw/");
    }
    
    public static String getProcessedDataPath() {
        return properties.getProperty("data.processed.path", "data/processed/");
    }
    
    public static String getModelsPath() {
        return properties.getProperty("data.models.path", "data/models/");
    }
    
    // CSV file names
    public static String getIcuStaysFile() {
        return getRawDataPath() + properties.getProperty("data.icustays.file");
    }
    
    public static String getPatientsFile() {
        return getRawDataPath() + properties.getProperty("data.patients.file");
    }
    
    public static String getAdmissionsFile() {
        return getRawDataPath() + properties.getProperty("data.admissions.file");
    }
    
    public static String getLabEventsFile() {
        return getRawDataPath() + properties.getProperty("data.labevents.file");
    }
    
    public static String getLabItemsFile() {
        return getRawDataPath() + properties.getProperty("data.labitems.file");
    }
    
    // Spark configuration
    public static String getSparkMaster() {
        return properties.getProperty("spark.master", "local[*]");
    }
    
    public static String getSparkAppName() {
        return properties.getProperty("spark.app.name", "PatientReadmissionETL");
    }
    
    // Model parameters
    public static int getReadmissionWindowDays() {
        return Integer.parseInt(properties.getProperty("model.readmission.window.days", "30"));
    }
}
