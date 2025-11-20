package com.healthcare.readmission.model.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "predictions", indexes = {
        @Index(name = "idx_predictions_patient", columnList = "patient_id"),
        @Index(name = "idx_predictions_user", columnList = "user_id"),
        @Index(name = "idx_predictions_created", columnList = "created_at")
})
public class Prediction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Link to patient (nullable for now)
    @Column(name = "patient_id")
    private Long patientId;

    // Who made the prediction
    @Column(name = "user_id")
    private Long userId;

    // Input data - Patient identifiers
    @Column(name = "subject_id")
    private Integer subjectId;

    @Column(name = "hadm_id")
    private Integer hadmId;

    @Column(name = "icustay_id")
    private Integer icustayId;

    // Input data - Demographics
    private Double age;

    @Column(length = 1)
    private String gender;

    @Column(name = "los")
    private Double los;

    @Column(length = 50)
    private String insurance;

    @Column(name = "marital_status", length = 50)
    private String maritalStatus;

    // Lab values - Glucose
    @Column(name = "glucose_min")
    private Double glucoseMin;

    @Column(name = "glucose_max")
    private Double glucoseMax;

    @Column(name = "glucose_mean")
    private Double glucoseMean;

    // Lab values - Calcium
    @Column(name = "calcium_min")
    private Double calciumMin;

    @Column(name = "calcium_max")
    private Double calciumMax;

    @Column(name = "calcium_mean")
    private Double calciumMean;

    // Lab values - Albumin
    @Column(name = "albumin_min")
    private Double albuminMin;

    @Column(name = "albumin_max")
    private Double albuminMax;

    @Column(name = "albumin_mean")
    private Double albuminMean;

    // Lab values - Platelet
    @Column(name = "platelet_min")
    private Double plateletMin;

    @Column(name = "platelet_max")
    private Double plateletMax;

    @Column(name = "platelet_mean")
    private Double plateletMean;

    // Lab values - Magnesium
    @Column(name = "magnesium_min")
    private Double magnesiumMin;

    @Column(name = "magnesium_max")
    private Double magnesiumMax;

    @Column(name = "magnesium_mean")
    private Double magnesiumMean;

    // Lab values - Urea
    @Column(name = "urea_min")
    private Double ureaMin;

    @Column(name = "urea_max")
    private Double ureaMax;

    @Column(name = "urea_mean")
    private Double ureaMean;

    // Prediction results
    @Column(name = "will_readmit")
    private Boolean willReadmit;

    @Column(name = "readmission_probability")
    private Double readmissionProbability;

    @Column(name = "no_readmission_probability")
    private Double noReadmissionProbability;

    @Column(name = "risk_level", length = 20)
    private String riskLevel;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Prediction() {
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Integer subjectId) {
        this.subjectId = subjectId;
    }

    public Integer getHadmId() {
        return hadmId;
    }

    public void setHadmId(Integer hadmId) {
        this.hadmId = hadmId;
    }

    public Integer getIcustayId() {
        return icustayId;
    }

    public void setIcustayId(Integer icustayId) {
        this.icustayId = icustayId;
    }

    public Double getAge() {
        return age;
    }

    public void setAge(Double age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Double getLos() {
        return los;
    }

    public void setLos(Double los) {
        this.los = los;
    }

    public String getInsurance() {
        return insurance;
    }

    public void setInsurance(String insurance) {
        this.insurance = insurance;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public Double getGlucoseMin() {
        return glucoseMin;
    }

    public void setGlucoseMin(Double glucoseMin) {
        this.glucoseMin = glucoseMin;
    }

    public Double getGlucoseMax() {
        return glucoseMax;
    }

    public void setGlucoseMax(Double glucoseMax) {
        this.glucoseMax = glucoseMax;
    }

    public Double getGlucoseMean() {
        return glucoseMean;
    }

    public void setGlucoseMean(Double glucoseMean) {
        this.glucoseMean = glucoseMean;
    }

    public Double getCalciumMin() {
        return calciumMin;
    }

    public void setCalciumMin(Double calciumMin) {
        this.calciumMin = calciumMin;
    }

    public Double getCalciumMax() {
        return calciumMax;
    }

    public void setCalciumMax(Double calciumMax) {
        this.calciumMax = calciumMax;
    }

    public Double getCalciumMean() {
        return calciumMean;
    }

    public void setCalciumMean(Double calciumMean) {
        this.calciumMean = calciumMean;
    }

    public Double getAlbuminMin() {
        return albuminMin;
    }

    public void setAlbuminMin(Double albuminMin) {
        this.albuminMin = albuminMin;
    }

    public Double getAlbuminMax() {
        return albuminMax;
    }

    public void setAlbuminMax(Double albuminMax) {
        this.albuminMax = albuminMax;
    }

    public Double getAlbuminMean() {
        return albuminMean;
    }

    public void setAlbuminMean(Double albuminMean) {
        this.albuminMean = albuminMean;
    }

    public Double getPlateletMin() {
        return plateletMin;
    }

    public void setPlateletMin(Double plateletMin) {
        this.plateletMin = plateletMin;
    }

    public Double getPlateletMax() {
        return plateletMax;
    }

    public void setPlateletMax(Double plateletMax) {
        this.plateletMax = plateletMax;
    }

    public Double getPlateletMean() {
        return plateletMean;
    }

    public void setPlateletMean(Double plateletMean) {
        this.plateletMean = plateletMean;
    }

    public Double getMagnesiumMin() {
        return magnesiumMin;
    }

    public void setMagnesiumMin(Double magnesiumMin) {
        this.magnesiumMin = magnesiumMin;
    }

    public Double getMagnesiumMax() {
        return magnesiumMax;
    }

    public void setMagnesiumMax(Double magnesiumMax) {
        this.magnesiumMax = magnesiumMax;
    }

    public Double getMagnesiumMean() {
        return magnesiumMean;
    }

    public void setMagnesiumMean(Double magnesiumMean) {
        this.magnesiumMean = magnesiumMean;
    }

    public Double getUreaMin() {
        return ureaMin;
    }

    public void setUreaMin(Double ureaMin) {
        this.ureaMin = ureaMin;
    }

    public Double getUreaMax() {
        return ureaMax;
    }

    public void setUreaMax(Double ureaMax) {
        this.ureaMax = ureaMax;
    }

    public Double getUreaMean() {
        return ureaMean;
    }

    public void setUreaMean(Double ureaMean) {
        this.ureaMean = ureaMean;
    }

    public Boolean getWillReadmit() {
        return willReadmit;
    }

    public void setWillReadmit(Boolean willReadmit) {
        this.willReadmit = willReadmit;
    }

    public Double getReadmissionProbability() {
        return readmissionProbability;
    }

    public void setReadmissionProbability(Double readmissionProbability) {
        this.readmissionProbability = readmissionProbability;
    }

    public Double getNoReadmissionProbability() {
        return noReadmissionProbability;
    }

    public void setNoReadmissionProbability(Double noReadmissionProbability) {
        this.noReadmissionProbability = noReadmissionProbability;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Prediction{id=" + id + ", subjectId=" + subjectId +
                ", willReadmit=" + willReadmit + ", probability=" + readmissionProbability + "}";
    }
}
