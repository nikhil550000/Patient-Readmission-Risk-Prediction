package com.healthcare.readmission;

import com.healthcare.readmission.config.AppConfig;
import com.healthcare.readmission.config.SparkConfig;
import org.apache.spark.sql.SparkSession;

public class TestConfig {
    public static void main(String[] args) {
        System.out.println("=== Testing Configuration Setup ===\n");
        
        // Test AppConfig
        System.out.println("Application Configuration:");
        System.out.println("  App Name: " + AppConfig.getAppName());
        System.out.println("  Raw Data Path: " + AppConfig.getRawDataPath());
        System.out.println("  ICU Stays File: " + AppConfig.getIcuStaysFile());
        System.out.println("  Patients File: " + AppConfig.getPatientsFile());
        System.out.println("  Readmission Window: " + AppConfig.getReadmissionWindowDays() + " days\n");
        
        // Test SparkConfig
        System.out.println("Initializing Spark Session...");
        SparkSession spark = SparkConfig.getSparkSession();
        
        System.out.println("\n=== Configuration Test Complete ===");
        
        // Cleanup
        SparkConfig.stopSparkSession();
    }
}
