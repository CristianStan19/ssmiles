package com.example.dcm_stellarsmiles.Classes.Schedule;

import java.util.List;

public class Schedule {
    private String doctorID;
    private String week; // Format: YYYY-WW (Year-Week)
    private List<String> availableDays;

    public Schedule() { }

    public Schedule(String doctorID, String week, List<String> availableDays) {
        this.doctorID = doctorID;
        this.week = week;
        this.availableDays = availableDays;
    }

    // Getters and setters
    public String getDoctorID() {
        return doctorID;
    }

    public void setDoctorID(String doctorID) {
        this.doctorID = doctorID;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public List<String> getAvailableDays() {
        return availableDays;
    }

    public void setAvailableDays(List<String> availableDays) {
        this.availableDays = availableDays;
    }
}
