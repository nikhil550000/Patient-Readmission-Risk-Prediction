package com.healthcare.readmission.dto;

/**
 * Patient data for prediction request
 */
public class PatientData {
    private Integer subjectId;
    private Integer hadmId;
    private Integer icuStayId;
    private Double age;
    private String gender;
    private Double los;
    private String insurance;
    private String maritalStatus;

    // Lab features
    private Double glucoseMin;
    private Double glucoseMax;
    private Double glucoseMean;
    private Double calciumMin;
    private Double calciumMax;
    private Double calciumMean;
    private Double albuminMin;
    private Double albuminMax;
    private Double albuminMean;
    private Double plateletMin;
    private Double plateletMax;
    private Double plateletMean;
    private Double magnesiumMin;
    private Double magnesiumMax;
    private Double magnesiumMean;
    private Double ureaMin;
    private Double ureaMax;
    private Double ureaMean;

    // Constructors
    public PatientData() {
    }

    // Getters and Setters
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

    public Integer getIcuStayId() {
        return icuStayId;
    }

    public void setIcuStayId(Integer icuStayId) {
        this.icuStayId = icuStayId;
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

    // Lab features getters/setters
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
}
