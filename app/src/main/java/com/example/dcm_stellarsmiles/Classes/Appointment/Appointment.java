package com.example.dcm_stellarsmiles.Classes.Appointment;

import com.example.dcm_stellarsmiles.Classes.Customer.Customer;
import com.example.dcm_stellarsmiles.Classes.Employees.Assistant;
import com.example.dcm_stellarsmiles.Classes.Employees.Doctor;
import com.example.dcm_stellarsmiles.Constants.Constants;
import com.example.dcm_stellarsmiles.Enum.AppointmentStatus;

public class Appointment {
    private String appointmentDate;
    private String type;
    private double cost;
    private Doctor doctor;
    private Assistant assistant;
    private Customer customer;

    public Appointment(String appointmentDate, String type, Doctor doctor, Assistant assistant, Customer customer) {
        this.appointmentDate = appointmentDate;
        this.type = type;
        this.cost = Constants.APPOINTMENT_COSTS.getOrDefault(type, 0.0);
        this.assistant = assistant;
        this.doctor = doctor;
        this.customer = customer;
        this.customer.setVisits(this.customer.getVisits() + 1);
        this.customer.setAppointmentStatus(AppointmentStatus.APPOINTMENT_ONGOING);
    }

    public String getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(String dateTime) {
        this.appointmentDate = dateTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getCost() {
        if(customer.getVisits() >= Constants.LOYALITY_REQUIRMENT) {
            return cost - cost * Constants.LOYALITY_DISCOUNT;
        }
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public Assistant getAssistant() {
        return assistant;
    }

    public void setAssistant(Assistant assistant) {
        this.assistant = assistant;
    }

    @Override
    public String toString() {
        return "Appointment{" +
                "dateTime=" + appointmentDate +
                ", type='" + type + '\'' +
                ", cost=" + cost +
                '}';
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        Appointment appointment = (Appointment) super.clone();
        appointment.setAssistant(this.assistant);
        appointment.setDoctor(this.doctor);
        appointment.setCost(this.cost);
        appointment.setAppointmentDate(this.appointmentDate);
        appointment.setType(this.type);
        return appointment;
    }
}
