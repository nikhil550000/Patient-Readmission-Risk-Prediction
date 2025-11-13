package com.healthcare.readmission.model;

import com.healthcare.readmission.config.AppConfig;
import com.healthcare.readmission.config.Constants;
import org.apache.spark.ml.Pipeline;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.ml.PipelineStage;
import org.apache.spark.ml.classification.GBTClassifier;
import org.apache.spark.ml.evaluation.BinaryClassificationEvaluator;
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator;
import org.apache.spark.ml.feature.StringIndexer;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.util.ArrayList;
import java.util.List;

/**
 * Gradient-Boosted Tree Classifier for readmission prediction
 * Includes proper categorical encoding for all text features
 */
public class ReadmissionModel {
    
    private PipelineModel trainedModel;
    private String[] featureColumns;
    
    /**
     * Stratified train-test split to preserve class distribution
     */
    public static class TrainTestSplit {
        public Dataset<Row> trainData;
        public Dataset<Row> testData;
        
        public TrainTestSplit(Dataset<Row> train, Dataset<Row> test) {
            this.trainData = train;
            this.testData = test;
        }
    }
    
    /**
     * Perform stratified split based on readmission label
     */
    public TrainTestSplit stratifiedSplit(Dataset<Row> data, double trainRatio) {
        System.out.println("\n=== Performing Stratified Train-Test Split ===\n");
        
        Dataset<Row> positives = data.filter(
            data.col(Constants.COL_READMISSION_30DAY).equalTo(1)
        );
        Dataset<Row> negatives = data.filter(
            data.col(Constants.COL_READMISSION_30DAY).equalTo(0)
        );
        
        long totalPositives = positives.count();
        long totalNegatives = negatives.count();
        
        System.out.println("Original class distribution:");
        System.out.println("  Positive cases (readmission=1): " + totalPositives);
        System.out.println("  Negative cases (readmission=0): " + totalNegatives);
        System.out.println("  Positive ratio: " + String.format("%.2f%%", 
            (totalPositives * 100.0) / (totalPositives + totalNegatives)));
        
        Dataset<Row>[] posSplit = positives.randomSplit(new double[]{trainRatio, 1 - trainRatio}, 42);
        Dataset<Row>[] negSplit = negatives.randomSplit(new double[]{trainRatio, 1 - trainRatio}, 42);
        
        Dataset<Row> trainData = posSplit[0].union(negSplit[0]);
        Dataset<Row> testData = posSplit[1].union(negSplit[1]);
        
        long trainPos = trainData.filter(
            trainData.col(Constants.COL_READMISSION_30DAY).equalTo(1)
        ).count();
        long trainNeg = trainData.filter(
            trainData.col(Constants.COL_READMISSION_30DAY).equalTo(0)
        ).count();
        
        long testPos = testData.filter(
            testData.col(Constants.COL_READMISSION_30DAY).equalTo(1)
        ).count();
        long testNeg = testData.filter(
            testData.col(Constants.COL_READMISSION_30DAY).equalTo(0)
        ).count();
        
        System.out.println("\nTrain set:");
        System.out.println("  Total: " + trainData.count());
        System.out.println("  Positive: " + trainPos + " (" + 
            String.format("%.2f%%", (trainPos * 100.0) / trainData.count()) + ")");
        System.out.println("  Negative: " + trainNeg);
        
        System.out.println("\nTest set:");
        System.out.println("  Total: " + testData.count());
        System.out.println("  Positive: " + testPos + " (" + 
            String.format("%.2f%%", (testPos * 100.0) / testData.count()) + ")");
        System.out.println("  Negative: " + testNeg);
        
        System.out.println("\n✓ Stratified split complete\n");
        
        return new TrainTestSplit(trainData, testData);
    }
    
    /**
     * Prepare feature columns for model training
     * Includes all numerical and encoded categorical features
     */
    private String[] prepareFeatureColumns(Dataset<Row> data) {
        List<String> features = new ArrayList<>();
        
        // Numerical features
        features.add(Constants.COL_AGE);
        features.add("LOS");
        
        // Lab features (18 total: 6 tests × 3 aggregations)
        for (String test : Constants.LAB_ITEMIDS.keySet()) {
            features.add(test + "_min");
            features.add(test + "_max");
            features.add(test + "_mean");
        }
        
        // Encoded categorical features
        features.add("GENDER_indexed");
        features.add("INSURANCE_indexed");
        features.add("MARITAL_STATUS_indexed");
        
        System.out.println("\nFeature columns (" + features.size() + " total):");
        System.out.println("  Numerical: AGE, LOS");
        System.out.println("  Lab features: 18 (6 tests × min/max/mean)");
        System.out.println("  Categorical (encoded): GENDER, INSURANCE, MARITAL_STATUS");
        
        return features.toArray(new String[0]);
    }
    
    /**
     * Train GBT Classifier model with proper categorical encoding
     */
    public PipelineModel train(Dataset<Row> trainingData) {
        System.out.println("\n=== Training Gradient-Boosted Tree Classifier ===\n");
        
        // Step 1: String indexers for all categorical features
        System.out.println("Setting up categorical encoders...");
        
        StringIndexer genderIndexer = new StringIndexer()
            .setInputCol(Constants.COL_GENDER)
            .setOutputCol("GENDER_indexed")
            .setHandleInvalid("keep"); // Keep unknown values
        
        StringIndexer insuranceIndexer = new StringIndexer()
            .setInputCol(Constants.COL_INSURANCE)
            .setOutputCol("INSURANCE_indexed")
            .setHandleInvalid("keep");
        
        StringIndexer maritalIndexer = new StringIndexer()
            .setInputCol(Constants.COL_MARITAL_STATUS)
            .setOutputCol("MARITAL_STATUS_indexed")
            .setHandleInvalid("keep");
        
        System.out.println("✓ Encoders configured");
        
        // Step 2: Prepare feature columns
        featureColumns = prepareFeatureColumns(trainingData);
        
        // Step 3: Assemble features into a vector
        System.out.println("\nAssembling feature vector...");
        VectorAssembler assembler = new VectorAssembler()
            .setInputCols(featureColumns)
            .setOutputCol("features")
            .setHandleInvalid("skip"); // Skip rows with null/invalid values
        
        System.out.println("✓ Feature assembler configured");
        
        // Step 4: Define GBT Classifier
        System.out.println("\nConfiguring GBT Classifier...");
        GBTClassifier gbt = new GBTClassifier()
            .setLabelCol(Constants.COL_READMISSION_30DAY)
            .setFeaturesCol("features")
            .setPredictionCol("prediction")
            .setMaxIter(20)
            .setMaxDepth(5)
            .setStepSize(0.1)
            .setSeed(42);
        
        System.out.println("  Max iterations: 20");
        System.out.println("  Max depth: 5");
        System.out.println("  Step size: 0.1");
        System.out.println("✓ Classifier configured");
        
        // Step 5: Create ML pipeline
        Pipeline pipeline = new Pipeline().setStages(new PipelineStage[]{
            genderIndexer,
            insuranceIndexer,
            maritalIndexer,
            assembler,
            gbt
        });
        
        // Step 6: Train model
        System.out.println("\nTraining model (this may take 1-2 minutes)...");
        long startTime = System.currentTimeMillis();
        trainedModel = pipeline.fit(trainingData);
        long endTime = System.currentTimeMillis();
        
        System.out.println("✓ Model trained successfully!");
        System.out.println("Training time: " + String.format("%.2f", (endTime - startTime) / 1000.0) + " seconds\n");
        
        return trainedModel;
    }
    
    /**
     * Evaluate model on test data
     */
    public void evaluate(Dataset<Row> testData) {
        System.out.println("\n=== Evaluating Model ===\n");
        
        // Make predictions
        Dataset<Row> predictions = trainedModel.transform(testData);
        
        // Binary classification metrics
        BinaryClassificationEvaluator evaluatorROC = new BinaryClassificationEvaluator()
            .setLabelCol(Constants.COL_READMISSION_30DAY)
            .setRawPredictionCol("rawPrediction")
            .setMetricName("areaUnderROC");
        
        double auc = evaluatorROC.evaluate(predictions);
        
        BinaryClassificationEvaluator evaluatorPR = new BinaryClassificationEvaluator()
            .setLabelCol(Constants.COL_READMISSION_30DAY)
            .setRawPredictionCol("rawPrediction")
            .setMetricName("areaUnderPR");
        
        double auprc = evaluatorPR.evaluate(predictions);
        
        // Multiclass classification metrics
        MulticlassClassificationEvaluator evaluatorAcc = new MulticlassClassificationEvaluator()
            .setLabelCol(Constants.COL_READMISSION_30DAY)
            .setPredictionCol("prediction")
            .setMetricName("accuracy");
        
        double accuracy = evaluatorAcc.evaluate(predictions);
        
        MulticlassClassificationEvaluator evaluatorF1 = new MulticlassClassificationEvaluator()
            .setLabelCol(Constants.COL_READMISSION_30DAY)
            .setPredictionCol("prediction")
            .setMetricName("f1");
        
        double f1 = evaluatorF1.evaluate(predictions);
        
        // Print results
        System.out.println("Model Performance Metrics:");
        System.out.println("========================================");
        System.out.println("  Area Under ROC:  " + String.format("%.4f", auc));
        System.out.println("  Area Under PR:   " + String.format("%.4f", auprc));
        System.out.println("  Accuracy:        " + String.format("%.4f", accuracy));
        System.out.println("  F1 Score:        " + String.format("%.4f", f1));
        System.out.println("========================================");
        
        // Confusion matrix
        System.out.println("\nConfusion Matrix:");
        System.out.println("(Actual=row, Predicted=column)");
        predictions.groupBy(Constants.COL_READMISSION_30DAY, "prediction").count()
            .orderBy(Constants.COL_READMISSION_30DAY, "prediction")
            .show();
        
        // Sample predictions
        System.out.println("Sample predictions (first 10 rows):");
        predictions.select(
            Constants.COL_SUBJECT_ID,
            Constants.COL_AGE,
            Constants.COL_GENDER,
            Constants.COL_READMISSION_30DAY,
            "prediction",
            "probability"
        ).show(10, false);
    }
    
    /**
     * Save trained model to disk
     */
    public void saveModel(String modelName) {
        String modelPath = AppConfig.getModelsPath() + modelName;
        
        System.out.println("\n=== Saving Model ===\n");
        System.out.println("Saving to: " + modelPath);
        
        try {
            trainedModel.write().overwrite().save(modelPath);
            System.out.println("✓ Model saved successfully!\n");
        } catch (Exception e) {
            System.err.println("Error saving model: " + e.getMessage());
        }
    }
    
    /**
     * Get trained model
     */
    public PipelineModel getModel() {
        return trainedModel;
    }
}
