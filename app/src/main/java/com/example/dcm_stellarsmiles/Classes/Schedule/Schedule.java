package com.example.dcm_stellarsmiles.Classes.Schedule;

import java.util.List;
import java.util.Map;

public class Schedule {
    private String doctorID;
    private String month; // Format: MMM YYYY (e.g., JUL 2024)
    private int year;
    private Map<String, List<String>> days; // Map of day -> list of intervals

    public Schedule() { }

    public Schedule(String doctorID, String month, int year, Map<String, List<String>> days) {
        this.doctorID = doctorID;
        this.month = month;
        this.year = year;
        this.days = days;
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

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public Map<String, List<String>> getDays() {
        return days;
    }

    public void setDays(Map<String, List<String>> days) {
        this.days = days;
    }
}
