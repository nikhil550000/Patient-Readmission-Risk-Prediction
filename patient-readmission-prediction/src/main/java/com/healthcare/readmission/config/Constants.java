package com.healthcare.readmission.config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Project-wide constants for Patient Readmission Prediction
 */
public class Constants {
    
    // Column names - ICUSTAYS
    public static final String COL_SUBJECT_ID = "SUBJECT_ID";
    public static final String COL_HADM_ID = "HADM_ID";
    public static final String COL_ICUSTAY_ID = "ICUSTAY_ID";
    public static final String COL_INTIME = "INTIME";
    public static final String COL_OUTTIME = "OUTTIME";
    public static final String COL_FIRST_CAREUNIT = "FIRST_CAREUNIT";
    
    // Column names - PATIENTS
    public static final String COL_GENDER = "GENDER";
    public static final String COL_DOB = "DOB";
    
    // Column names - ADMISSIONS
    public static final String COL_ADMITTIME = "ADMITTIME";
    public static final String COL_DISCHTIME = "DISCHTIME";
    public static final String COL_DEATHTIME = "DEATHTIME";
    public static final String COL_INSURANCE = "INSURANCE";
    public static final String COL_MARITAL_STATUS = "MARITAL_STATUS";
    
    // Column names - LABEVENTS
    public static final String COL_ITEMID = "ITEMID";
    public static final String COL_CHARTTIME = "CHARTTIME";
    public static final String COL_VALUENUM = "VALUENUM";
    
    // Engineered features
    public static final String COL_AGE = "AGE";
    public static final String COL_READMISSION_30DAY = "readmission_30day";
    
    // Care unit filter
    public static final String CARE_UNIT_MICU = "MICU";
    

    // Lab test ITEMIDs mapping (from your Python code)
    public static final Map<String, List<Integer>> LAB_ITEMIDS = new HashMap<String, List<Integer>>() {{
        put("urea", Arrays.asList(51006));
        put("platelet", Arrays.asList(51265));
        put("magnesium", Arrays.asList(50960));
        put("albumin", Arrays.asList(50862));
        put("calcium", Arrays.asList(50893, 50808));
        put("glucose", Arrays.asList(50809, 50931));
    }};
    
    // Date format
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    
    // Age thresholds (MIMIC anonymization)
    public static final double AGE_THRESHOLD = 89.0;
    public static final double AGE_DEFAULT = 90.0;
    public static final double AGE_MAX = 200.0;

    // Column names - Additional categorical
    public static final String COL_LOS = "LOS";

}
