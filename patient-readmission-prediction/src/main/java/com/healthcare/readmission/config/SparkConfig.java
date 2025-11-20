package com.healthcare.readmission.config;

import org.apache.spark.sql.SparkSession;

/**
 * Spark session configuration with HDFS integration
 */
public class SparkConfig {

    private static SparkSession sparkSession = null;

    /**
     * Get or create Spark session with HDFS support
     */
    public static SparkSession getSparkSession() {
        if (sparkSession == null) {
            sparkSession = SparkSession.builder()
                    .appName(AppConfig.getSparkAppName())
                    .master(AppConfig.getSparkMaster())
                    .config("spark.sql.adaptive.enabled", "true")
                    .config("spark.sql.adaptive.coalescePartitions.enabled", "true")
                    .config("spark.driver.memory", "4g")
                    .config("spark.executor.memory", "4g")
                    // HDFS Configuration
                    .config("spark.hadoop.fs.defaultFS", "hdfs://localhost:9000")
                    .config("spark.hadoop.dfs.replication", "1")
                    // Fix for ancient dates in MIMIC-III
                    .config("spark.sql.parquet.int96RebaseModeInWrite", "CORRECTED")
                    .config("spark.sql.parquet.datetimeRebaseModeInWrite", "CORRECTED")
                    // Java 17+ compatibility fixes
                    .config("spark.driver.extraJavaOptions",
                            "--add-opens=java.base/java.lang=ALL-UNNAMED " +
                                    "--add-opens=java.base/java.lang.invoke=ALL-UNNAMED " +
                                    "--add-opens=java.base/java.lang.reflect=ALL-UNNAMED " +
                                    "--add-opens=java.base/java.io=ALL-UNNAMED " +
                                    "--add-opens=java.base/java.net=ALL-UNNAMED " +
                                    "--add-opens=java.base/java.nio=ALL-UNNAMED " +
                                    "--add-opens=java.base/java.util=ALL-UNNAMED " +
                                    "--add-opens=java.base/java.util.concurrent=ALL-UNNAMED " +
                                    "--add-opens=java.base/java.util.concurrent.atomic=ALL-UNNAMED " +
                                    "--add-opens=java.base/sun.nio.ch=ALL-UNNAMED " +
                                    "--add-opens=java.base/sun.nio.cs=ALL-UNNAMED " +
                                    "--add-opens=java.base/sun.security.action=ALL-UNNAMED " +
                                    "--add-opens=java.base/sun.util.calendar=ALL-UNNAMED " +
                                    "--add-opens=java.security.jgss/sun.security.krb5=ALL-UNNAMED")
                    .config("spark.executor.extraJavaOptions",
                            "--add-opens=java.base/java.lang=ALL-UNNAMED " +
                                    "--add-opens=java.base/java.lang.invoke=ALL-UNNAMED " +
                                    "--add-opens=java.base/java.lang.reflect=ALL-UNNAMED " +
                                    "--add-opens=java.base/java.io=ALL-UNNAMED " +
                                    "--add-opens=java.base/java.net=ALL-UNNAMED " +
                                    "--add-opens=java.base/java.nio=ALL-UNNAMED " +
                                    "--add-opens=java.base/java.util=ALL-UNNAMED " +
                                    "--add-opens=java.base/java.util.concurrent=ALL-UNNAMED " +
                                    "--add-opens=java.base/java.util.concurrent.atomic=ALL-UNNAMED " +
                                    "--add-opens=java.base/sun.nio.ch=ALL-UNNAMED " +
                                    "--add-opens=java.base/sun.nio.cs=ALL-UNNAMED " +
                                    "--add-opens=java.base/sun.security.action=ALL-UNNAMED " +
                                    "--add-opens=java.base/sun.util.calendar=ALL-UNNAMED " +
                                    "--add-opens=java.security.jgss/sun.security.krb5=ALL-UNNAMED")
                    .getOrCreate();

            // Set log level to reduce noise
            sparkSession.sparkContext().setLogLevel("WARN");

            System.out.println("Spark Session initialized with HDFS support:");
            System.out.println("  App Name: " + AppConfig.getSparkAppName());
            System.out.println("  Master: " + AppConfig.getSparkMaster());
            System.out.println("  Spark Version: " + sparkSession.version());
            System.out.println("  Hadoop Version: " + org.apache.hadoop.util.VersionInfo.getVersion());
            System.out.println("  HDFS Enabled: Yes");
            System.out.println("  HDFS NameNode: hdfs://localhost:9000");
        }
        return sparkSession;
    }

    /**
     * Stop Spark session
     */
    public static void stopSparkSession() {
        if (sparkSession != null) {
            sparkSession.stop();
            sparkSession = null;
            System.out.println("Spark Session stopped.");
        }
    }
}
