package com.healthcare.readmission;

import org.apache.spark.sql.SparkSession;

public class HelloWorldspark {
    public static void main(String[] args) {
        SparkSession spark = SparkSession.builder()
                .appName("HelloSpark")
                .master("local[*]")
                .getOrCreate();

        System.out.println("Hello, Spark! Running version: " + spark.version());

        // Stop Spark session
        spark.stop();
    }
}
