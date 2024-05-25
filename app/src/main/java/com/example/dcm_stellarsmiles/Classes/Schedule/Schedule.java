package com.example.dcm_stellarsmiles.Classes.Schedule;

import java.util.List;

public class Schedule {
    private String doctorID;
    private String month; // Format: YYYY-WW (Year-Week)
    private List<String> availableDays;

    public Schedule() { }

    public Schedule(String doctorID, String month, List<String> availableDays) {
        this.doctorID = doctorID;
        this.month = month;
        this.availableDays = availableDays;
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

    public void setMonth(String week) {
        this.month = week;
    }

    public List<String> getAvailableDays() {
        return availableDays;
    }

    public void setAvailableDays(List<String> availableDays) {
        this.availableDays = availableDays;
    }
}
