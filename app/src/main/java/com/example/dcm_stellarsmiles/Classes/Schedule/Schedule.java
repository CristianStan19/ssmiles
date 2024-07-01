package com.example.dcm_stellarsmiles.Classes.Schedule;

import java.util.List;
import java.util.Map;

public class Schedule {
    private String doctorID;
    private String date; // Format: dd-MM-yyyy (e.g., 01-07-2024)
    private Map<String, List<String>> days; // Map of date -> list of intervals

    public Schedule() { }

    public Schedule(String doctorID, String date, Map<String, List<String>> days) {
        this.doctorID = doctorID;
        this.date = date;
        this.days = days;
    }

    // Getters and setters
    public String getDoctorID() {
        return doctorID;
    }

    public void setDoctorID(String doctorID) {
        this.doctorID = doctorID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Map<String, List<String>> getDays() {
        return days;
    }

    public void setDays(Map<String, List<String>> days) {
        this.days = days;
    }
}
