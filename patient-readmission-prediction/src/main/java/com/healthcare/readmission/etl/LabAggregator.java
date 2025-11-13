package com.healthcare.readmission.etl;

import com.healthcare.readmission.config.Constants;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.Column;

import java.util.HashMap;
import java.util.Map;

import static org.apache.spark.sql.functions.*;

/**
 * Lab feature aggregation for ICU stays
 */
public class LabAggregator {
    
    /**
     * Aggregate lab values (min, max, mean) for each ICU stay
     */
    public Dataset<Row> aggregateLabFeatures(Dataset<Row> icuStays, Dataset<Row> labEvents) {
        
        System.out.println("\n=== Starting Lab Feature Aggregation ===\n");
        
        // Step 1: Prepare labevents
        System.out.println("Preparing lab events data...");
        labEvents = labEvents.withColumn(Constants.COL_CHARTTIME, 
                                         to_timestamp(col(Constants.COL_CHARTTIME)));
        
        // Get all relevant ITEMIDs
        java.util.List<Integer> allItemIds = new java.util.ArrayList<>();
        for (java.util.List<Integer> ids : Constants.LAB_ITEMIDS.values()) {
            allItemIds.addAll(ids);
        }
        
        // Filter labevents to only relevant ITEMIDs
        labEvents = labEvents.filter(col(Constants.COL_ITEMID).isin(allItemIds.toArray()));
        System.out.println("✓ Lab events filtered to relevant ITEMIDs: " + labEvents.count() + " rows");
        
        // Step 2: Add lab test name column
        labEvents = labEvents.withColumn("lab_test", 
            when(col(Constants.COL_ITEMID).isin(Constants.LAB_ITEMIDS.get("urea").toArray()), "urea")
            .when(col(Constants.COL_ITEMID).isin(Constants.LAB_ITEMIDS.get("platelet").toArray()), "platelet")
            .when(col(Constants.COL_ITEMID).isin(Constants.LAB_ITEMIDS.get("magnesium").toArray()), "magnesium")
            .when(col(Constants.COL_ITEMID).isin(Constants.LAB_ITEMIDS.get("albumin").toArray()), "albumin")
            .when(col(Constants.COL_ITEMID).isin(Constants.LAB_ITEMIDS.get("calcium").toArray()), "calcium")
            .when(col(Constants.COL_ITEMID).isin(Constants.LAB_ITEMIDS.get("glucose").toArray()), "glucose")
            .otherwise("unknown")
        );
        
        // Step 3: Join ICU stays with lab events
        System.out.println("Joining ICU stays with lab events (this may take time)...");
        Dataset<Row> joined = icuStays.alias("icu").join(
            labEvents.alias("lab"),
            col("icu." + Constants.COL_SUBJECT_ID).equalTo(col("lab." + Constants.COL_SUBJECT_ID))
                .and(col("lab." + Constants.COL_CHARTTIME).geq(col("icu." + Constants.COL_INTIME)))
                .and(col("lab." + Constants.COL_CHARTTIME).leq(col("icu." + Constants.COL_OUTTIME))),
            "left"
        );
        
        System.out.println("✓ Join complete");
        
        // Step 4: Aggregate min, max, mean for each ICU stay and lab test
        System.out.println("Aggregating lab values per ICU stay...");
        Dataset<Row> aggregated = joined.groupBy(
            col("icu." + Constants.COL_ICUSTAY_ID).as("icustay_id_agg"),
            col("lab.lab_test")
        ).agg(
            min(col("lab." + Constants.COL_VALUENUM)).cast("double").as("min_value"),
            max(col("lab." + Constants.COL_VALUENUM)).cast("double").as("max_value"),
            avg(col("lab." + Constants.COL_VALUENUM)).cast("double").as("mean_value")
        ).filter(col("lab_test").isNotNull());
        
        // Step 5: Process each lab test individually and join back
        System.out.println("Pivoting lab test results...");
        Dataset<Row> result = icuStays;
        
        for (String test : Constants.LAB_ITEMIDS.keySet()) {
            // Filter for this specific test
            Dataset<Row> testData = aggregated
                .filter(col("lab_test").equalTo(test))
                .select(
                    col("icustay_id_agg").as(Constants.COL_ICUSTAY_ID),
                    col("min_value").as(test + "_min"),
                    col("max_value").as(test + "_max"),
                    col("mean_value").as(test + "_mean")
                );
            
            // Join with result, being explicit about column references
            result = result.join(
                testData,
                result.col(Constants.COL_ICUSTAY_ID).equalTo(testData.col(Constants.COL_ICUSTAY_ID)),
                "left"
            ).drop(testData.col(Constants.COL_ICUSTAY_ID)); // Drop the duplicate ICUSTAY_ID from testData
        }
        
        // Step 6: Show statistics on lab data availability
        System.out.println("\n✓ Lab feature aggregation completed!");
        System.out.println("\nLab data availability:");
        long totalStays = result.count();
        
        for (String test : Constants.LAB_ITEMIDS.keySet()) {
            long countWithData = result.filter(col(test + "_min").isNotNull()).count();
            double percentage = (countWithData * 100.0) / totalStays;
            System.out.println(String.format("  %s: %d ICU stays (%.1f%%)", 
                                            test, countWithData, percentage));
        }
        
        System.out.println();
        return result;
    }
}
