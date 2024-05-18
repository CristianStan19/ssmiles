package com.example.dcm_stellarsmiles.Constants;

import java.util.HashMap;
import java.util.Map;

public class Constants {
    public static final String DEFAULT_POSITION = "Employee";
    public static final String APP_ON_GOING = "ongoing";
    public static final String APP_CANCELED = "canceled";
    public static final String APP_COMPLETED = "completed";

    public static final String ROUTINE_CHECKUP = "Routine Checkup";
    public static final String TOOTH_EXTRACTION = "Tooth Extraction";
    public static final String ROOT_CANAL_TREATMENT = "Root Canal Treatment";

    public static final int DEFAULT_COST_ROUTINE_CHECKUP = 50;
    public static final int DEFAULT_COST_TOOTH_EXTRACTION = 100;
    public static final int DEFAULT_COST_ROOT_CANAL_TREATMENT = 200;

    public static final Map<String, Integer> APPOINTMENT_COSTS = new HashMap<>();
    static {
        APPOINTMENT_COSTS.put(ROUTINE_CHECKUP, DEFAULT_COST_ROUTINE_CHECKUP);
        APPOINTMENT_COSTS.put(TOOTH_EXTRACTION, DEFAULT_COST_TOOTH_EXTRACTION);
        APPOINTMENT_COSTS.put(ROOT_CANAL_TREATMENT, DEFAULT_COST_ROOT_CANAL_TREATMENT);
    }

    public static final float LOYALITY_DISCOUNT = 0.1f;
    public static final int LOYALITY_REQUIRMENT = 5;
}
