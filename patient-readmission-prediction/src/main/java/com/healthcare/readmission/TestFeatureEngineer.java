package com.healthcare.readmission;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import com.healthcare.readmission.config.SparkConfig;
import com.healthcare.readmission.etl.DataLoader;
import com.healthcare.readmission.etl.DataMerger;
import com.healthcare.readmission.etl.FeatureEngineer;

public class TestFeatureEngineer {
    public static void main(String[] args) {
        System.out.println("=== Testing FeatureEngineer ===\n");
        
        // Load and merge data
        DataLoader loader = new DataLoader();
        DataLoader.MimicData data = loader.loadAllData();
        
        DataMerger merger = new DataMerger();
        Dataset<Row> mergedData = merger.mergeCoreData(
            data.icuStays,
            data.patients,
            data.admissions
        );
        
        // Apply feature engineering and filtering
        FeatureEngineer engineer = new FeatureEngineer();
        Dataset<Row> processedData = engineer.calculateAgeAndFilter(mergedData);
        
        // Show sample of processed data
        System.out.println("Processed Data Sample (first 5 rows):");
        processedData.select("SUBJECT_ID", "HADM_ID", "GENDER", "AGE", "FIRST_CAREUNIT", "INTIME", "OUTTIME")
                     .show(5, false);
        
        System.out.println("\n=== FeatureEngineer Test Complete ===");
        
        // Cleanup
        SparkConfig.stopSparkSession();
    }
}
