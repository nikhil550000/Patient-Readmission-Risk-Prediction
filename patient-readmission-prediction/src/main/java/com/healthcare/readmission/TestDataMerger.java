package com.healthcare.readmission;

import com.healthcare.readmission.config.SparkConfig;
import com.healthcare.readmission.etl.DataLoader;
import com.healthcare.readmission.etl.DataMerger;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

public class TestDataMerger {
    public static void main(String[] args) {
        System.out.println("=== Testing DataMerger ===\n");
        
        // Load data
        DataLoader loader = new DataLoader();
        DataLoader.MimicData data = loader.loadAllData();
        
        // Merge data
        DataMerger merger = new DataMerger();
        Dataset<Row> mergedData = merger.mergeCoreData(
            data.icuStays,
            data.patients,
            data.admissions
        );
        
        // Show column names
        System.out.println("Merged DataFrame Columns:");
        for (String col : mergedData.columns()) {
            System.out.println("  - " + col);
        }
        
        System.out.println("\n=== DataMerger Test Complete ===");
        
        // Cleanup
        SparkConfig.stopSparkSession();
    }
}
