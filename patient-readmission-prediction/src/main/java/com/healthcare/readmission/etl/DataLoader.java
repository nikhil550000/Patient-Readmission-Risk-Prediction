package com.healthcare.readmission.etl;

import com.healthcare.readmission.config.AppConfig;
import com.healthcare.readmission.config.Constants;
import com.healthcare.readmission.config.SparkConfig;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

/**
 * Data loader for MIMIC-III CSV files
 * Corresponds to Python Step 1: Loading core tables
 */
public class DataLoader {

    private final SparkSession spark;

    public DataLoader() {
        this.spark = SparkConfig.getSparkSession();
    }

    /**
     * Load ICUSTAYS table
     */
    public Dataset<Row> loadIcuStays() {
        System.out.println("Loading ICUSTAYS...");
        Dataset<Row> df = spark.read()
                .option("header", "true")
                .option("inferSchema", "true")
                .csv(AppConfig.getIcuStaysFile());

        System.out.println("✓ ICUSTAYS loaded: " + df.count() + " rows");
        return df;
    }

    /**
     * Load PATIENTS table
     */
    public Dataset<Row> loadPatients() {
        System.out.println("Loading PATIENTS...");
        Dataset<Row> df = spark.read()
                .option("header", "true")
                .option("inferSchema", "true")
                .csv(AppConfig.getPatientsFile());

        System.out.println("✓ PATIENTS loaded: " + df.count() + " rows");
        return df;
    }

    /**
     * Load ADMISSIONS table
     */
    public Dataset<Row> loadAdmissions() {
        System.out.println("Loading ADMISSIONS...");
        Dataset<Row> df = spark.read()
                .option("header", "true")
                .option("inferSchema", "true")
                .csv(AppConfig.getAdmissionsFile());

        System.out.println("✓ ADMISSIONS loaded: " + df.count() + " rows");
        return df;
    }

    /**
     * Load LABEVENTS table (large file - may take time)
     */
    public Dataset<Row> loadLabEvents() {
        System.out.println("Loading LABEVENTS (this may take 30-60 seconds)...");
        Dataset<Row> df = spark.read()
                .option("header", "true")
                .option("inferSchema", "true")
                .csv(AppConfig.getLabEventsFile());

        System.out.println("✓ LABEVENTS loaded: " + df.count() + " rows");
        return df;
    }

    /**
     * Load D_LABITEMS dictionary
     */
    public Dataset<Row> loadLabItems() {
        System.out.println("Loading D_LABITEMS...");
        Dataset<Row> df = spark.read()
                .option("header", "true")
                .option("inferSchema", "true")
                .csv(AppConfig.getLabItemsFile());

        System.out.println("✓ D_LABITEMS loaded: " + df.count() + " rows");
        return df;
    }

    /**
     * Load all core tables at once
     */
    public MimicData loadAllData() {
        System.out.println("\n=== Loading Core MIMIC-III Tables ===\n");

        MimicData data = new MimicData();
        data.icuStays = loadIcuStays();
        data.patients = loadPatients();
        data.admissions = loadAdmissions();
        data.labEvents = loadLabEvents();
        data.labItems = loadLabItems();

        System.out.println("\n✓ All tables loaded successfully!\n");
        return data;
    }

    /**
     * Container class for all MIMIC-III datasets
     */
    public static class MimicData {
        public Dataset<Row> icuStays;
        public Dataset<Row> patients;
        public Dataset<Row> admissions;
        public Dataset<Row> labEvents;
        public Dataset<Row> labItems;
    }
}
