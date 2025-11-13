package com.healthcare.readmission;

import com.healthcare.readmission.config.SparkConfig;
import com.healthcare.readmission.etl.*;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

public class TestCompleteETL {
    public static void main(String[] args) {
        System.out.println("=== Complete ETL Pipeline Test ===\n");
        
        // Step 1: Load all data
        DataLoader loader = new DataLoader();
        DataLoader.MimicData data = loader.loadAllData();
        
        // Step 2: Merge core tables
        DataMerger merger = new DataMerger();
        Dataset<Row> mergedData = merger.mergeCoreData(
            data.icuStays,
            data.patients,
            data.admissions
        );
        
        // Step 3 & 4: Calculate age and filter
        FeatureEngineer engineer = new FeatureEngineer();
        Dataset<Row> filteredData = engineer.calculateAgeAndFilter(mergedData);
        
        // Step 5: Aggregate lab features
        LabAggregator labAggregator = new LabAggregator();
        Dataset<Row> withLabFeatures = labAggregator.aggregateLabFeatures(
            filteredData,
            data.labEvents
        );
        
        // Step 6: Generate readmission labels
        LabelGenerator labelGenerator = new LabelGenerator();
        Dataset<Row> finalData = labelGenerator.generateReadmissionLabels(withLabFeatures);
        
        // Show final statistics
        System.out.println("=== Final Dataset Summary ===\n");
        System.out.println("Total rows: " + finalData.count());
        System.out.println("Total columns: " + finalData.columns().length);
        System.out.println("\nColumn names:");
        for (String col : finalData.columns()) {
            System.out.println("  - " + col);
        }
        
        // Show sample of final processed data
        System.out.println("\n\nFinal processed data sample (first 5 rows):");
        finalData.select(
            "SUBJECT_ID", "HADM_ID", "AGE", "GENDER",
            "urea_mean", "glucose_mean", "platelet_mean",
            "readmission_30day"
        ).show(5, false);
        
        // Show readmission distribution
        System.out.println("\nReadmission label distribution:");
        finalData.groupBy("readmission_30day").count().show();
        
        System.out.println("\n=== Complete ETL Pipeline Test Complete ===");
        System.out.println("All 6 preprocessing steps successfully executed!");
        
        // Cleanup
        SparkConfig.stopSparkSession();
    }
}
