package com.example.dcm_stellarsmiles.Classes.Price;

public class PriceItem {
    private String appointmentType;
    private int cost;

    public PriceItem(String appointmentType, int cost) {
        this.appointmentType = appointmentType;
        this.cost = cost;
    }

    public String getAppointmentType() {
        return appointmentType;
    }

    public int getCost() {
        return cost;
    }
}
