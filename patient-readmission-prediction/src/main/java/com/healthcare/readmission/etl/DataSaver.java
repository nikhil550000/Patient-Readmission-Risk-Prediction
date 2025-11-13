package com.healthcare.readmission.etl;

import com.healthcare.readmission.config.AppConfig;
import com.healthcare.readmission.config.Constants;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;

import java.util.ArrayList;
import java.util.List;

import static org.apache.spark.sql.functions.col;

/**
 * Save processed data to disk
 */
public class DataSaver {
    
    /**
     * Select only ML-relevant columns
     */
    private Dataset<Row> selectMLColumns(Dataset<Row> data) {
        List<String> mlColumns = new ArrayList<>();
        
        // Identifiers
        mlColumns.add(Constants.COL_SUBJECT_ID);
        mlColumns.add(Constants.COL_HADM_ID);
        mlColumns.add(Constants.COL_ICUSTAY_ID);
        
        // Demographics
        mlColumns.add(Constants.COL_AGE);
        mlColumns.add(Constants.COL_GENDER);
        
        // Temporal features
        mlColumns.add("LOS"); // Length of stay
        
        // Categorical (we'll keep these for now, can be encoded later)
        mlColumns.add(Constants.COL_INSURANCE);
        mlColumns.add(Constants.COL_MARITAL_STATUS);
        
        // Lab features
        for (String test : Constants.LAB_ITEMIDS.keySet()) {
            mlColumns.add(test + "_min");
            mlColumns.add(test + "_max");
            mlColumns.add(test + "_mean");
        }
        
        // Target variable
        mlColumns.add(Constants.COL_READMISSION_30DAY);
        
        // Select only existing columns (some may be null)
        List<String> existingColumns = new ArrayList<>();
        for (String column : mlColumns) {
            try {
                data.col(column);
                existingColumns.add(column);
            } catch (Exception e) {
                // Column doesn't exist, skip it
            }
        }
        
        System.out.println("Selecting " + existingColumns.size() + " ML-relevant columns");
        return data.select(existingColumns.stream()
            .map(c -> col(c))
            .toArray(org.apache.spark.sql.Column[]::new));
    }
    
    /**
     * Save processed dataset in Parquet format (more efficient for Spark)
     */
    public void saveProcessedDataParquet(Dataset<Row> data, String filename) {
        String outputPath = AppConfig.getProcessedDataPath() + filename;
        
        System.out.println("\n=== Saving Processed Data (Parquet) ===\n");
        System.out.println("Saving to: " + outputPath);
        
        // Select only ML columns (drop text and date columns)
        Dataset<Row> mlData = selectMLColumns(data);
        
        System.out.println("Columns to save:");
        for (String col : mlData.columns()) {
            System.out.println("  - " + col);
        }
        
        // Save as Parquet (columnar format, compressed, efficient)
        mlData.write()
            .mode(SaveMode.Overwrite)
            .parquet(outputPath);
        
        System.out.println("\n✓ Data saved successfully in Parquet format!");
        System.out.println("Total rows saved: " + mlData.count());
        System.out.println();
    }
    
    /**
     * Save processed dataset to CSV format
     */
    public void saveProcessedDataCSV(Dataset<Row> data, String filename) {
        String outputPath = AppConfig.getProcessedDataPath() + filename;
        
        System.out.println("\n=== Saving Processed Data (CSV) ===\n");
        System.out.println("Saving to: " + outputPath);
        
        // Select only ML columns
        Dataset<Row> mlData = selectMLColumns(data);
        
        // Save as single CSV file with header
        mlData.coalesce(1)
            .write()
            .mode(SaveMode.Overwrite)
            .option("header", "true")
            .csv(outputPath);
        
        System.out.println("✓ Data saved successfully!");
        System.out.println("Total rows saved: " + mlData.count());
        System.out.println();
    }
}
