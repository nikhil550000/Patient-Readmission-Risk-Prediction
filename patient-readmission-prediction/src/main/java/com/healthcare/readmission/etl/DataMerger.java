package com.healthcare.readmission.etl;

import com.healthcare.readmission.config.Constants;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import static org.apache.spark.sql.functions.*;

/**
 * Data merger for MIMIC-III tables
 * Corresponds Step 2: Merging demographics and admission data
 */
public class DataMerger {

    /**
     * Merge ICUSTAYS with PATIENTS and ADMISSIONS
     * Step 2 in the ETL pipeline
     */
    public Dataset<Row> mergeCoreData(Dataset<Row> icuStays,
            Dataset<Row> patients,
            Dataset<Row> admissions) {

        System.out.println("\n=== Merging Demographics and Admission Data ===\n");

        // Step 1: Merge ICUSTAYS with PATIENTS on SUBJECT_ID
        System.out.println("Merging ICUSTAYS with PATIENTS...");
        Dataset<Row> patientsSelected = patients.select(
                col(Constants.COL_SUBJECT_ID),
                col(Constants.COL_GENDER),
                col(Constants.COL_DOB));

        Dataset<Row> df = icuStays.join(
                patientsSelected,
                icuStays.col(Constants.COL_SUBJECT_ID)
                        .equalTo(patientsSelected.col(Constants.COL_SUBJECT_ID)),
                "left").drop(patientsSelected.col(Constants.COL_SUBJECT_ID)); // Drop duplicate SUBJECT_ID column

        System.out.println("✓ Merged PATIENTS: " + df.count() + " rows");

        // Step 2: Merge with ADMISSIONS on HADM_ID
        System.out.println("Merging with ADMISSIONS...");
        Dataset<Row> admissionsSelected = admissions.select(
                col(Constants.COL_HADM_ID),
                col(Constants.COL_ADMITTIME),
                col(Constants.COL_DISCHTIME),
                col(Constants.COL_DEATHTIME),
                col(Constants.COL_INSURANCE),
                col(Constants.COL_MARITAL_STATUS));

        df = df.join(
                admissionsSelected,
                df.col(Constants.COL_HADM_ID)
                        .equalTo(admissionsSelected.col(Constants.COL_HADM_ID)),
                "left").drop(admissionsSelected.col(Constants.COL_HADM_ID)); // Drop duplicate HADM_ID column

        System.out.println("✓ Merged ADMISSIONS: " + df.count() + " rows");

        // Convert datetime columns to timestamp type
        System.out.println("Converting datetime columns...");
        String[] dateColumns = {
                Constants.COL_INTIME,
                Constants.COL_OUTTIME,
                Constants.COL_DOB,
                Constants.COL_ADMITTIME,
                Constants.COL_DISCHTIME,
                Constants.COL_DEATHTIME
        };

        for (String colName : dateColumns) {
            df = df.withColumn(colName, to_timestamp(col(colName)));
        }

        System.out.println("✓ Datetime columns converted");

        System.out.println("\nDataframe structure after merges:");
        df.printSchema();

        System.out.println("\nSample merged data (first 3 rows):");
        df.show(3, false);

        System.out.println("\n✓ Merge complete!\n");

        return df;
    }
}
