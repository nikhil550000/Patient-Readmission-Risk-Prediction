package com.healthcare.readmission.etl;

import com.healthcare.readmission.config.AppConfig;
import com.healthcare.readmission.config.Constants;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.expressions.Window;
import org.apache.spark.sql.expressions.WindowSpec;

import static org.apache.spark.sql.functions.*;

/**
 * Generate 30-day readmission labels
 * Corresponds Step 6: Calculate readmission labels
 */
public class LabelGenerator {

    /**
     * Calculate 30-day readmission labels using window functions
     * Much more efficient than Python's iterative approach
     */
    public Dataset<Row> generateReadmissionLabels(Dataset<Row> df) {

        System.out.println("\n=== Calculating 30-Day Readmission Labels ===\n");

        // Define window spec: partition by patient, order by admission time
        WindowSpec windowSpec = Window
                .partitionBy(Constants.COL_SUBJECT_ID)
                .orderBy(col(Constants.COL_INTIME));

        // Get the next ICU stay's INTIME for the same patient using lead() function
        df = df.withColumn("next_intime", lead(col(Constants.COL_INTIME), 1).over(windowSpec));

        // Calculate days between current OUTTIME and next INTIME
        df = df.withColumn("days_to_next_admission",
                datediff(col("next_intime"), col(Constants.COL_OUTTIME)));

        // Create readmission label: 1 if next admission is within 30 days, 0 otherwise
        df = df.withColumn(Constants.COL_READMISSION_30DAY,
                when(col("days_to_next_admission").isNotNull()
                        .and(col("days_to_next_admission").leq(AppConfig.getReadmissionWindowDays())), 1)
                        .otherwise(0));

        // Drop temporary columns
        df = df.drop("next_intime", "days_to_next_admission");

        // Show readmission statistics
        long totalStays = df.count();
        long positiveReadmissions = df.filter(col(Constants.COL_READMISSION_30DAY).equalTo(1)).count();
        long negativeReadmissions = df.filter(col(Constants.COL_READMISSION_30DAY).equalTo(0)).count();
        double positivePercentage = (positiveReadmissions * 100.0) / totalStays;

        System.out.println("✓ Readmission labels created");
        System.out.println("Total ICU stays: " + totalStays);
        System.out.println("Positive readmissions within 30 days: " + positiveReadmissions +
                " (" + String.format("%.2f", positivePercentage) + "%)");
        System.out.println("Negative cases (no readmission): " + negativeReadmissions);
        System.out.println();

        return df;
    }
}
