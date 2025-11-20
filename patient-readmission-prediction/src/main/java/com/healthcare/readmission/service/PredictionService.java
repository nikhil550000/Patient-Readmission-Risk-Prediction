package com.healthcare.readmission.service;

import com.healthcare.readmission.config.AppConfig;
import com.healthcare.readmission.config.SparkConfig;
import com.healthcare.readmission.dto.PatientData;
import com.healthcare.readmission.dto.PredictionResult;
import com.healthcare.readmission.model.entity.Prediction;
import com.healthcare.readmission.model.repository.PredictionRepository;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class PredictionService {

    @Autowired
    private PredictionRepository predictionRepository;

    private PipelineModel model;
    private SparkSession spark;
    private StructType schema;
    private boolean initialized = false;

    @PostConstruct
    private void init() {
        try {
            System.out.println("Initializing PredictionService...");
            this.spark = SparkConfig.getSparkSession();
            loadModel();
            createSchema();
            this.initialized = true;
            System.out.println("✓ PredictionService initialized successfully");
        } catch (Exception e) {
            System.err.println("⚠ Warning: PredictionService initialization failed: " + e.getMessage());
            System.err.println("  Model predictions will not be available until this is resolved.");
        }
    }

    private void loadModel() {
        String modelPath = AppConfig.getModelsPath() + "readmission_gbt_model";
        System.out.println("Loading model from: " + modelPath);
        this.model = PipelineModel.load(modelPath);
        System.out.println("✓ Model loaded successfully from HDFS");
    }

    private void createSchema() {
        this.schema = new StructType(new StructField[] {
                new StructField("SUBJECT_ID", DataTypes.IntegerType, true, Metadata.empty()),
                new StructField("HADM_ID", DataTypes.IntegerType, true, Metadata.empty()),
                new StructField("ICUSTAY_ID", DataTypes.IntegerType, true, Metadata.empty()),
                new StructField("AGE", DataTypes.DoubleType, true, Metadata.empty()),
                new StructField("GENDER", DataTypes.StringType, true, Metadata.empty()),
                new StructField("LOS", DataTypes.DoubleType, true, Metadata.empty()),
                new StructField("INSURANCE", DataTypes.StringType, true, Metadata.empty()),
                new StructField("MARITAL_STATUS", DataTypes.StringType, true, Metadata.empty()),
                new StructField("glucose_min", DataTypes.DoubleType, true, Metadata.empty()),
                new StructField("glucose_max", DataTypes.DoubleType, true, Metadata.empty()),
                new StructField("glucose_mean", DataTypes.DoubleType, true, Metadata.empty()),
                new StructField("calcium_min", DataTypes.DoubleType, true, Metadata.empty()),
                new StructField("calcium_max", DataTypes.DoubleType, true, Metadata.empty()),
                new StructField("calcium_mean", DataTypes.DoubleType, true, Metadata.empty()),
                new StructField("albumin_min", DataTypes.DoubleType, true, Metadata.empty()),
                new StructField("albumin_max", DataTypes.DoubleType, true, Metadata.empty()),
                new StructField("albumin_mean", DataTypes.DoubleType, true, Metadata.empty()),
                new StructField("platelet_min", DataTypes.DoubleType, true, Metadata.empty()),
                new StructField("platelet_max", DataTypes.DoubleType, true, Metadata.empty()),
                new StructField("platelet_mean", DataTypes.DoubleType, true, Metadata.empty()),
                new StructField("magnesium_min", DataTypes.DoubleType, true, Metadata.empty()),
                new StructField("magnesium_max", DataTypes.DoubleType, true, Metadata.empty()),
                new StructField("magnesium_mean", DataTypes.DoubleType, true, Metadata.empty()),
                new StructField("urea_min", DataTypes.DoubleType, true, Metadata.empty()),
                new StructField("urea_max", DataTypes.DoubleType, true, Metadata.empty()),
                new StructField("urea_mean", DataTypes.DoubleType, true, Metadata.empty())
        });
    }

    public boolean isReady() {
        return initialized && model != null;
    }

    /**
     * Make prediction and save to database
     */
    public PredictionResult predictAndSave(PatientData patientData, Long userId) {
        if (!initialized || model == null) {
            throw new RuntimeException("Prediction service not initialized. Model not loaded.");
        }

        try {
            // Make ML prediction
            Row row = RowFactory.create(
                    patientData.getSubjectId(), patientData.getHadmId(), patientData.getIcuStayId(),
                    patientData.getAge(), patientData.getGender(), patientData.getLos(),
                    patientData.getInsurance(), patientData.getMaritalStatus(),
                    patientData.getGlucoseMin(), patientData.getGlucoseMax(), patientData.getGlucoseMean(),
                    patientData.getCalciumMin(), patientData.getCalciumMax(), patientData.getCalciumMean(),
                    patientData.getAlbuminMin(), patientData.getAlbuminMax(), patientData.getAlbuminMean(),
                    patientData.getPlateletMin(), patientData.getPlateletMax(), patientData.getPlateletMean(),
                    patientData.getMagnesiumMin(), patientData.getMagnesiumMax(), patientData.getMagnesiumMean(),
                    patientData.getUreaMin(), patientData.getUreaMax(), patientData.getUreaMean());

            List<Row> data = Arrays.asList(row);
            Dataset<Row> inputDataset = spark.createDataFrame(data, schema);
            Dataset<Row> predictions = model.transform(inputDataset);

            Row result = predictions.select("prediction", "probability").first();
            double prediction = result.getDouble(0);
            org.apache.spark.ml.linalg.Vector probability = (org.apache.spark.ml.linalg.Vector) result.get(1);

            boolean willReadmit = prediction == 1.0;
            double readmitProb = probability.toArray()[1];
            double noReadmitProb = probability.toArray()[0];

            // Save to database
            Prediction predictionEntity = new Prediction();
            predictionEntity.setUserId(userId);
            predictionEntity.setSubjectId(patientData.getSubjectId());
            predictionEntity.setHadmId(patientData.getHadmId());
            predictionEntity.setIcustayId(patientData.getIcuStayId());
            predictionEntity.setAge(patientData.getAge());
            predictionEntity.setGender(patientData.getGender());
            predictionEntity.setLos(patientData.getLos());
            predictionEntity.setInsurance(patientData.getInsurance());
            predictionEntity.setMaritalStatus(patientData.getMaritalStatus());

            // Lab values
            predictionEntity.setGlucoseMin(patientData.getGlucoseMin());
            predictionEntity.setGlucoseMax(patientData.getGlucoseMax());
            predictionEntity.setGlucoseMean(patientData.getGlucoseMean());
            predictionEntity.setCalciumMin(patientData.getCalciumMin());
            predictionEntity.setCalciumMax(patientData.getCalciumMax());
            predictionEntity.setCalciumMean(patientData.getCalciumMean());
            predictionEntity.setAlbuminMin(patientData.getAlbuminMin());
            predictionEntity.setAlbuminMax(patientData.getAlbuminMax());
            predictionEntity.setAlbuminMean(patientData.getAlbuminMean());
            predictionEntity.setPlateletMin(patientData.getPlateletMin());
            predictionEntity.setPlateletMax(patientData.getPlateletMax());
            predictionEntity.setPlateletMean(patientData.getPlateletMean());
            predictionEntity.setMagnesiumMin(patientData.getMagnesiumMin());
            predictionEntity.setMagnesiumMax(patientData.getMagnesiumMax());
            predictionEntity.setMagnesiumMean(patientData.getMagnesiumMean());
            predictionEntity.setUreaMin(patientData.getUreaMin());
            predictionEntity.setUreaMax(patientData.getUreaMax());
            predictionEntity.setUreaMean(patientData.getUreaMean());

            // Prediction results
            predictionEntity.setWillReadmit(willReadmit);
            predictionEntity.setReadmissionProbability(readmitProb);
            predictionEntity.setNoReadmissionProbability(noReadmitProb);
            predictionEntity.setRiskLevel(calculateRiskLevel(readmitProb));
            predictionEntity.setCreatedAt(LocalDateTime.now());

            predictionRepository.save(predictionEntity);

            return new PredictionResult(
                    patientData.getSubjectId(),
                    patientData.getIcuStayId(),
                    willReadmit,
                    readmitProb,
                    noReadmitProb);

        } catch (Exception e) {
            System.err.println("Prediction error: " + e.getMessage());
            throw new RuntimeException("Prediction failed", e);
        }
    }

    /**
     * Get prediction history for a patient
     */
    public List<Prediction> getPatientPredictions(Integer subjectId) {
        return predictionRepository.findBySubjectId(subjectId);
    }

    /**
     * Get prediction history for a user
     */
    public List<Prediction> getUserPredictions(Long userId) {
        return predictionRepository.findByUserId(userId);
    }

    /**
     * Get recent predictions
     */
    public List<Prediction> getRecentPredictions() {
        return predictionRepository.findTop10ByOrderByCreatedAtDesc();
    }

    /**
     * Get prediction by ID
     */
    public Optional<Prediction> getPredictionById(Long id) {
        return predictionRepository.findById(id);
    }

    /**
     * Calculate risk level based on probability
     */
    private String calculateRiskLevel(double probability) {
        if (probability < 0.3) {
            return "LOW";
        } else if (probability < 0.7) {
            return "MEDIUM";
        } else {
            return "HIGH";
        }
    }
}
