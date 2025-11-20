package com.healthcare.readmission.dto;

/**
 * Prediction result response
 */
public class PredictionResult {
    private Integer subjectId;
    private Integer icuStayId;
    private boolean willReadmit;
    private double readmissionProbability;
    private double noReadmissionProbability;
    private String riskLevel;

    public PredictionResult(Integer subjectId, Integer icuStayId,
            boolean willReadmit, double readmissionProb, double noReadmissionProb) {
        this.subjectId = subjectId;
        this.icuStayId = icuStayId;
        this.willReadmit = willReadmit;
        this.readmissionProbability = readmissionProb;
        this.noReadmissionProbability = noReadmissionProb;
        this.riskLevel = calculateRiskLevel(readmissionProb);
    }

    private String calculateRiskLevel(double prob) {
        if (prob < 0.3)
            return "LOW";
        else if (prob < 0.6)
            return "MEDIUM";
        else
            return "HIGH";
    }

    // Getters
    public Integer getSubjectId() {
        return subjectId;
    }

    public Integer getIcuStayId() {
        return icuStayId;
    }

    public boolean isWillReadmit() {
        return willReadmit;
    }

    public double getReadmissionProbability() {
        return readmissionProbability;
    }

    public double getNoReadmissionProbability() {
        return noReadmissionProbability;
    }

    public String getRiskLevel() {
        return riskLevel;
    }
}
