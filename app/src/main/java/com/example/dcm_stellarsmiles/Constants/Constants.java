package com.example.dcm_stellarsmiles.Constants;

import java.util.HashMap;
import java.util.Map;

public class Constants {
    public static final String DEFAULT_POSITION = "Employee";
    public static final String APP_ON_GOING = "ongoing";
    public static final String APP_CANCELED = "canceled";
    public static final String APP_COMPLETED = "completed";

    // General Dentist Appointments
    public static final String ROUTINE_CHECKUP = "Routine Checkup";
    public static final String TOOTH_EXTRACTION = "Tooth Extraction";
    public static final String DENTAL_FILLING = "Dental Filling";
    public static final String TEETH_CLEANING = "Teeth Cleaning";
    public static final String X_RAY = "X-Ray";

    // Endodontist Appointments
    public static final String ROOT_CANAL_TREATMENT = "Root Canal Treatment";
    public static final String APICOECTOMY = "Apicoectomy";
    public static final String PULPOTOMY = "Pulpotomy";
    public static final String ENDODONTIC_REASSESSMENT = "Endodontic Reassessment";
    public static final String INTERNAL_BLEACHING = "Internal Bleaching";

    // Orthodontist Appointments
    public static final String BRACES_INSTALLATION = "Braces Installation";
    public static final String BRACES_ADJUSTMENT = "Braces Adjustment";
    public static final String RETAINER_INSTALLATION = "Retainer Installation";
    public static final String RETAINER_ADJUSTMENT = "Retainer Adjustment";
    public static final String INVISALIGN_TREATMENT = "Invisalign Treatment";

    // Periodontist Appointments
    public static final String GUM_TREATMENT = "Gum Treatment";
    public static final String DENTAL_IMPLANT = "Dental Implant";
    public static final String BONE_GRAFTING = "Bone Grafting";
    public static final String GUM_GRAFTING = "Gum Grafting";
    public static final String SCALING_AND_ROOT_PLANING = "Scaling and Root Planing";

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
    public static final Map<String, String> APPOINTMENT_SPECIALIZATIONS = new HashMap<>();

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

        // General Dentist Specializations
        APPOINTMENT_SPECIALIZATIONS.put(ROUTINE_CHECKUP, "General Dentist");
        APPOINTMENT_SPECIALIZATIONS.put(TOOTH_EXTRACTION, "General Dentist");
        APPOINTMENT_SPECIALIZATIONS.put(DENTAL_FILLING, "General Dentist");
        APPOINTMENT_SPECIALIZATIONS.put(TEETH_CLEANING, "General Dentist");
        APPOINTMENT_SPECIALIZATIONS.put(X_RAY, "General Dentist");

        // Endodontist Specializations
        APPOINTMENT_SPECIALIZATIONS.put(ROOT_CANAL_TREATMENT, "Endodontist");
        APPOINTMENT_SPECIALIZATIONS.put(APICOECTOMY, "Endodontist");
        APPOINTMENT_SPECIALIZATIONS.put(PULPOTOMY, "Endodontist");
        APPOINTMENT_SPECIALIZATIONS.put(ENDODONTIC_REASSESSMENT, "Endodontist");
        APPOINTMENT_SPECIALIZATIONS.put(INTERNAL_BLEACHING, "Endodontist");

        // Orthodontist Specializations
        APPOINTMENT_SPECIALIZATIONS.put(BRACES_INSTALLATION, "Orthodontist");
        APPOINTMENT_SPECIALIZATIONS.put(BRACES_ADJUSTMENT, "Orthodontist");
        APPOINTMENT_SPECIALIZATIONS.put(RETAINER_INSTALLATION, "Orthodontist");
        APPOINTMENT_SPECIALIZATIONS.put(RETAINER_ADJUSTMENT, "Orthodontist");
        APPOINTMENT_SPECIALIZATIONS.put(INVISALIGN_TREATMENT, "Orthodontist");

        // Periodontist Specializations
        APPOINTMENT_SPECIALIZATIONS.put(GUM_TREATMENT, "Periodontist");
        APPOINTMENT_SPECIALIZATIONS.put(DENTAL_IMPLANT, "Periodontist");
        APPOINTMENT_SPECIALIZATIONS.put(BONE_GRAFTING, "Periodontist");
        APPOINTMENT_SPECIALIZATIONS.put(GUM_GRAFTING, "Periodontist");
        APPOINTMENT_SPECIALIZATIONS.put(SCALING_AND_ROOT_PLANING, "Periodontist");
    }

    public static final float LOYALITY_DISCOUNT = 0.1f;
    public static final int LOYALITY_REQUIRMENT = 5;
}
