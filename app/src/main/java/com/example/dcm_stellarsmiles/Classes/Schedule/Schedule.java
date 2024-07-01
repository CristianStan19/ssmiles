package com.example.dcm_stellarsmiles.Classes.Schedule;

import java.util.List;
import java.util.Map;

public class Schedule {
    private String doctorID;
    private String month; // Format: YYYY-MM
    private Map<String, List<String>> availableDaysAndIntervals; // Map of week -> list of "day-interval"

    public Schedule() { }

    public Schedule(String doctorID, String month, Map<String, List<String>> availableDaysAndIntervals) {
        this.doctorID = doctorID;
        this.month = month;
        this.availableDaysAndIntervals = availableDaysAndIntervals;
    }

    // Getters and setters
    public String getDoctorID() {
        return doctorID;
    }

    public void setDoctorID(String doctorID) {
        this.doctorID = doctorID;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public Map<String, List<String>> getAvailableDaysAndIntervals() {
        return availableDaysAndIntervals;
    }

}
