package com.healthcare.readmission;

import com.healthcare.readmission.config.SparkConfig;
import com.healthcare.readmission.etl.DataLoader;

public class TestDataLoader {
    public static void main(String[] args) {
        System.out.println("=== Testing DataLoader ===\n");
        
        // Initialize data loader
        DataLoader loader = new DataLoader();
        
        // Load all data
        DataLoader.MimicData data = loader.loadAllData();
        
        // Show sample data from each table
        System.out.println("=== Sample Data Preview ===\n");
        
        System.out.println("ICUSTAYS (first 3 rows):");
        data.icuStays.show(3, false);
        
        System.out.println("\nPATIENTS (first 3 rows):");
        data.patients.show(3, false);
        
        System.out.println("\nADMISSIONS (first 3 rows):");
        data.admissions.show(3, false);
        
        System.out.println("\n=== DataLoader Test Complete ===");
        
        // Cleanup
        SparkConfig.stopSparkSession();
    }
}
