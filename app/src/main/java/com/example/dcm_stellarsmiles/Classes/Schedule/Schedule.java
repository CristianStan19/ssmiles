package com.example.dcm_stellarsmiles.Classes.Schedule;

import java.util.List;
import java.util.Map;

public class Schedule {
    private String doctorName;
    private String monthYear;
    private Map<String, List<String>> days; // Available intervals
    private Map<String, Boolean> unavailableDays; // Unavailable days

    // Required empty constructor for Firestore
    public Schedule() {}

    public Schedule(String doctorName, String monthYear, Map<String, List<String>> days, Map<String, Boolean> unavailableDays) {
        this.doctorName = doctorName;
        this.monthYear = monthYear;
        this.days = days;
        this.unavailableDays = unavailableDays;
    }

    // Getters and setters
    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }
    public String getMonthYear() { return monthYear; }
    public void setMonthYear(String monthYear) { this.monthYear = monthYear; }
    public Map<String, List<String>> getDays() { return days; }
    public void setDays(Map<String, List<String>> days) { this.days = days; }
    public Map<String, Boolean> getUnavailableDays() { return unavailableDays; }
    public void setUnavailableDays(Map<String, Boolean> unavailableDays) { this.unavailableDays = unavailableDays; }
}