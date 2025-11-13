package com.healthcare.readmission;

import com.healthcare.readmission.config.SparkConfig;
import com.healthcare.readmission.etl.*;
import com.healthcare.readmission.model.ReadmissionModel;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

/**
 * Main pipeline: ETL + Model Training with Stratified Split
 */
public class MainETLAndTrain {
    public static void main(String[] args) {
        System.out.println("=== Patient Readmission Prediction - Complete Pipeline ===\n");
        
        // ===== ETL PIPELINE =====
        
        // Step 1-2: Load and merge data
        DataLoader loader = new DataLoader();
        DataLoader.MimicData data = loader.loadAllData();
        
        DataMerger merger = new DataMerger();
        Dataset<Row> mergedData = merger.mergeCoreData(
            data.icuStays, data.patients, data.admissions
        );
        
        // Step 3-4: Feature engineering and filtering
        FeatureEngineer engineer = new FeatureEngineer();
        Dataset<Row> filteredData = engineer.calculateAgeAndFilter(mergedData);
        
        // Step 5: Lab aggregation
        LabAggregator labAggregator = new LabAggregator();
        Dataset<Row> withLabFeatures = labAggregator.aggregateLabFeatures(
            filteredData, data.labEvents
        );
        
        // Step 6: Readmission labels
        LabelGenerator labelGenerator = new LabelGenerator();
        Dataset<Row> finalData = labelGenerator.generateReadmissionLabels(withLabFeatures);
        
        // Step 7: Save processed data
        DataSaver saver = new DataSaver();
        saver.saveProcessedDataParquet(finalData, "processed_readmission_data");
        
        // ===== MODEL TRAINING =====
        
        // Initialize model
        ReadmissionModel model = new ReadmissionModel();
        
        // Stratified split: 80% training, 20% test
        ReadmissionModel.TrainTestSplit split = model.stratifiedSplit(finalData, 0.8);
        
        // Train model
        model.train(split.trainData);
        
        // Evaluate model
        model.evaluate(split.testData);
        
        // Save model
        model.saveModel("readmission_gbt_model");
        
        System.out.println("\n=== Pipeline Complete ===");
        System.out.println("✓ ETL processed: " + finalData.count() + " records");
        System.out.println("✓ Model trained and saved with stratified split");
        System.out.println("✓ Ready for predictions!\n");
        
        // Cleanup
        SparkConfig.stopSparkSession();
    }
}
