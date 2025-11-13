package com.healthcare.readmission;

import com.healthcare.readmission.config.SparkConfig;
import com.healthcare.readmission.etl.DataLoader;
import com.healthcare.readmission.etl.DataMerger;
import com.healthcare.readmission.etl.FeatureEngineer;
import com.healthcare.readmission.etl.LabAggregator;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

public class TestLabAggregator {
    public static void main(String[] args) {
        System.out.println("=== Testing LabAggregator ===\n");
        
        // Load and process data through Steps 1-4
        DataLoader loader = new DataLoader();
        DataLoader.MimicData data = loader.loadAllData();
        
        DataMerger merger = new DataMerger();
        Dataset<Row> mergedData = merger.mergeCoreData(
            data.icuStays,
            data.patients,
            data.admissions
        );
        
        FeatureEngineer engineer = new FeatureEngineer();
        Dataset<Row> processedData = engineer.calculateAgeAndFilter(mergedData);
        
        // Step 5: Aggregate lab features
        LabAggregator labAggregator = new LabAggregator();
        Dataset<Row> withLabFeatures = labAggregator.aggregateLabFeatures(
            processedData,
            data.labEvents
        );
        
        // Show sample with lab features
        System.out.println("Sample data with lab features (first 3 rows):");
        withLabFeatures.select(
            "SUBJECT_ID", "HADM_ID", "AGE", "GENDER",
            "urea_min", "urea_max", "urea_mean",
            "glucose_min", "glucose_max", "glucose_mean"
        ).show(3, false);
        
        System.out.println("\nAll columns in final dataset:");
        for (String col : withLabFeatures.columns()) {
            System.out.println("  - " + col);
        }
        
        System.out.println("\n=== LabAggregator Test Complete ===");
        
        // Cleanup
        SparkConfig.stopSparkSession();
    }
}
