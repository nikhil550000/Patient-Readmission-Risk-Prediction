package com.healthcare.readmission.etl;

import com.healthcare.readmission.config.Constants;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.expressions.UserDefinedFunction;
import org.apache.spark.sql.types.DataTypes;

import static org.apache.spark.sql.functions.*;

/**
 * Feature engineering and dataset filtering
 * Corresponds to Python Steps 3 & 4: Age calculation and filtering
 */
public class FeatureEngineer {
    
    /**
     * Calculate AGE at admission and apply dataset filters
     * Python Steps 3 & 4 combined
     */
    public Dataset<Row> calculateAgeAndFilter(Dataset<Row> df) {
        
        System.out.println("\n=== Calculating Age and Filtering Dataset ===\n");
        
        long initialCount = df.count();
        System.out.println("Starting with: " + initialCount + " ICU stays");
        
        // Step 3: Calculate AGE
        df = calculateAge(df);
        
        // Step 4: Apply filters
        df = applyFilters(df, initialCount);
        
        return df;
    }
    
    /**
     * Calculate patient age at admission
     * Handles MIMIC-III date shifting for patients > 89 years
     */
    private Dataset<Row> calculateAge(Dataset<Row> df) {
        System.out.println("\nCalculating age at admission...");
        
        // Calculate age in years: (ADMITTIME - DOB) / 365.25 days
        df = df.withColumn(Constants.COL_AGE,
            datediff(col(Constants.COL_ADMITTIME), col(Constants.COL_DOB))
                .divide(365.25)
        );
        
        // Handle MIMIC anonymization: ages > 89 or negative are set to 90
        df = df.withColumn(Constants.COL_AGE,
            when(col(Constants.COL_AGE).lt(0).or(col(Constants.COL_AGE).gt(Constants.AGE_MAX)),
                Constants.AGE_DEFAULT)
            .otherwise(col(Constants.COL_AGE))
        );
        
        // Cap ages > 89 to 90
        df = df.withColumn(Constants.COL_AGE,
            when(col(Constants.COL_AGE).gt(Constants.AGE_THRESHOLD), Constants.AGE_DEFAULT)
            .otherwise(col(Constants.COL_AGE))
        );
        
        // Show age statistics
        Row stats = df.select(
            min(Constants.COL_AGE).as("min_age"),
            max(Constants.COL_AGE).as("max_age"),
            avg(Constants.COL_AGE).as("mean_age")
        ).first();
        
        System.out.println("✓ Age calculated");
        System.out.println("Age range: " + String.format("%.1f", stats.getDouble(0)) + 
                         " to " + String.format("%.1f", stats.getDouble(1)) + " years");
        System.out.println("Mean age: " + String.format("%.1f", stats.getDouble(2)) + " years");
        System.out.println("Missing ages: " + df.filter(col(Constants.COL_AGE).isNull()).count());
        
        return df;
    }
    
    /**
     * Apply filtering logic following report methodology
     */
    private Dataset<Row> applyFilters(Dataset<Row> df, long initialCount) {
        System.out.println("\nFiltering dataset following report methodology...");
        
        // Filter 1: Remove patients who died during hospital stay
        df = df.filter(col(Constants.COL_DEATHTIME).isNull());
        long afterDeaths = df.count();
        System.out.println("After removing deaths: " + afterDeaths + " rows (" + 
                         (initialCount - afterDeaths) + " removed)");
        
        // Filter 2: Keep only MICU (Medical ICU) stays
        df = df.filter(col(Constants.COL_FIRST_CAREUNIT).equalTo(Constants.CARE_UNIT_MICU));
        long afterMICU = df.count();
        System.out.println("After filtering to MICU only: " + afterMICU + " rows");
        
        // Filter 3: Remove stays with missing critical data
        df = df.na().drop(new String[]{
            Constants.COL_INTIME,
            Constants.COL_OUTTIME,
            Constants.COL_AGE,
            Constants.COL_GENDER
        });
        long afterMissing = df.count();
        System.out.println("After removing missing values: " + afterMissing + " rows");
        
        System.out.println("\n✓ Filtering complete: " + afterMissing + " ICU stays remaining");
        
        // Show final age statistics
        Row stats = df.select(
            min(Constants.COL_AGE).as("min_age"),
            max(Constants.COL_AGE).as("max_age"),
            avg(Constants.COL_AGE).as("mean_age")
        ).first();
        
        System.out.println("\nRemaining age stats:");
        System.out.println("  Age range: " + String.format("%.1f", stats.getDouble(0)) + 
                         " to " + String.format("%.1f", stats.getDouble(1)) + " years");
        System.out.println("  Mean age: " + String.format("%.1f", stats.getDouble(2)) + " years\n");
        
        return df;
    }
}
