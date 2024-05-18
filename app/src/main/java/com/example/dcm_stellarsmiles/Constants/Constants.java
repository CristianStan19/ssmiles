package com.example.dcm_stellarsmiles.Constants;

import java.util.HashMap;
import java.util.Map;

public class Constants {
    public static final String DEFAULT_POSITION = "Employee";
    public static final String APP_ON_GOING = "ongoing";
    public static final String APP_CANCELED = "canceled";
    public static final String APP_COMPLETED = "completed";

    // General Dentist Appointments
    public static final String ROUTINE_CHECKUP = "Routine Checkup"; // General Dentist
    public static final String TOOTH_EXTRACTION = "Tooth Extraction"; // General Dentist
    public static final String DENTAL_FILLING = "Dental Filling"; // General Dentist
    public static final String TEETH_CLEANING = "Teeth Cleaning"; // General Dentist
    public static final String X_RAY = "X-Ray"; // General Dentist

    // Endodontist Appointments
    public static final String ROOT_CANAL_TREATMENT = "Root Canal Treatment"; // Endodontist
    public static final String APICOECTOMY = "Apicoectomy"; // Endodontist
    public static final String PULPOTOMY = "Pulpotomy"; // Endodontist
    public static final String ENDODONTIC_REASSESSMENT = "Endodontic Reassessment"; // Endodontist
    public static final String INTERNAL_BLEACHING = "Internal Bleaching"; // Endodontist

    // Orthodontist Appointments
    public static final String BRACES_INSTALLATION = "Braces Installation"; // Orthodontist
    public static final String BRACES_ADJUSTMENT = "Braces Adjustment"; // Orthodontist
    public static final String RETAINER_INSTALLATION = "Retainer Installation"; // Orthodontist
    public static final String RETAINER_ADJUSTMENT = "Retainer Adjustment"; // Orthodontist
    public static final String INVISALIGN_TREATMENT = "Invisalign Treatment"; // Orthodontist

    // Periodontist Appointments
    public static final String GUM_TREATMENT = "Gum Treatment"; // Periodontist
    public static final String DENTAL_IMPLANT = "Dental Implant"; // Periodontist
    public static final String BONE_GRAFTING = "Bone Grafting"; // Periodontist
    public static final String GUM_GRAFTING = "Gum Grafting"; // Periodontist
    public static final String SCALING_AND_ROOT_PLANING = "Scaling and Root Planing"; // Periodontist

    // Costs for General Dentist Appointments
    public static final int DEFAULT_COST_ROUTINE_CHECKUP = 50;
    public static final int DEFAULT_COST_TOOTH_EXTRACTION = 100;
    public static final int DEFAULT_COST_DENTAL_FILLING = 75;
    public static final int DEFAULT_COST_TEETH_CLEANING = 80;
    public static final int DEFAULT_COST_X_RAY = 30;

    // Costs for Endodontist Appointments
    public static final int DEFAULT_COST_ROOT_CANAL_TREATMENT = 200;
    public static final int DEFAULT_COST_APICOECTOMY = 300;
    public static final int DEFAULT_COST_PULPOTOMY = 150;
    public static final int DEFAULT_COST_ENDODONTIC_REASSESSMENT = 100;
    public static final int DEFAULT_COST_INTERNAL_BLEACHING = 250;

    // Costs for Orthodontist Appointments
    public static final int DEFAULT_COST_BRACES_INSTALLATION = 3000;
    public static final int DEFAULT_COST_BRACES_ADJUSTMENT = 100;
    public static final int DEFAULT_COST_RETAINER_INSTALLATION = 500;
    public static final int DEFAULT_COST_RETAINER_ADJUSTMENT = 75;
    public static final int DEFAULT_COST_INVISALIGN_TREATMENT = 3500;

    // Costs for Periodontist Appointments
    public static final int DEFAULT_COST_GUM_TREATMENT = 150;
    public static final int DEFAULT_COST_DENTAL_IMPLANT = 2500;
    public static final int DEFAULT_COST_BONE_GRAFTING = 600;
    public static final int DEFAULT_COST_GUM_GRAFTING = 400;
    public static final int DEFAULT_COST_SCALING_AND_ROOT_PLANING = 200;

    public static final Map<String, Integer> APPOINTMENT_COSTS = new HashMap<>();
    static {
        // General Dentist Appointments
        APPOINTMENT_COSTS.put(ROUTINE_CHECKUP, DEFAULT_COST_ROUTINE_CHECKUP);
        APPOINTMENT_COSTS.put(TOOTH_EXTRACTION, DEFAULT_COST_TOOTH_EXTRACTION);
        APPOINTMENT_COSTS.put(DENTAL_FILLING, DEFAULT_COST_DENTAL_FILLING);
        APPOINTMENT_COSTS.put(TEETH_CLEANING, DEFAULT_COST_TEETH_CLEANING);
        APPOINTMENT_COSTS.put(X_RAY, DEFAULT_COST_X_RAY);

        // Endodontist Appointments
        APPOINTMENT_COSTS.put(ROOT_CANAL_TREATMENT, DEFAULT_COST_ROOT_CANAL_TREATMENT);
        APPOINTMENT_COSTS.put(APICOECTOMY, DEFAULT_COST_APICOECTOMY);
        APPOINTMENT_COSTS.put(PULPOTOMY, DEFAULT_COST_PULPOTOMY);
        APPOINTMENT_COSTS.put(ENDODONTIC_REASSESSMENT, DEFAULT_COST_ENDODONTIC_REASSESSMENT);
        APPOINTMENT_COSTS.put(INTERNAL_BLEACHING, DEFAULT_COST_INTERNAL_BLEACHING);

        // Orthodontist Appointments
        APPOINTMENT_COSTS.put(BRACES_INSTALLATION, DEFAULT_COST_BRACES_INSTALLATION);
        APPOINTMENT_COSTS.put(BRACES_ADJUSTMENT, DEFAULT_COST_BRACES_ADJUSTMENT);
        APPOINTMENT_COSTS.put(RETAINER_INSTALLATION, DEFAULT_COST_RETAINER_INSTALLATION);
        APPOINTMENT_COSTS.put(RETAINER_ADJUSTMENT, DEFAULT_COST_RETAINER_ADJUSTMENT);
        APPOINTMENT_COSTS.put(INVISALIGN_TREATMENT, DEFAULT_COST_INVISALIGN_TREATMENT);

        // Periodontist Appointments
        APPOINTMENT_COSTS.put(GUM_TREATMENT, DEFAULT_COST_GUM_TREATMENT);
        APPOINTMENT_COSTS.put(DENTAL_IMPLANT, DEFAULT_COST_DENTAL_IMPLANT);
        APPOINTMENT_COSTS.put(BONE_GRAFTING, DEFAULT_COST_BONE_GRAFTING);
        APPOINTMENT_COSTS.put(GUM_GRAFTING, DEFAULT_COST_GUM_GRAFTING);
        APPOINTMENT_COSTS.put(SCALING_AND_ROOT_PLANING, DEFAULT_COST_SCALING_AND_ROOT_PLANING);
    }

    public static final float LOYALITY_DISCOUNT = 0.1f;
    public static final int LOYALITY_REQUIRMENT = 5;
}
