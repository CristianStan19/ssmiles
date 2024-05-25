package com.example.dcm_stellarsmiles.Classes.Appointment;

import com.example.dcm_stellarsmiles.Classes.Customer.Customer;
import com.example.dcm_stellarsmiles.Classes.Employees.Assistant;
import com.example.dcm_stellarsmiles.Classes.Employees.Doctor;
import com.example.dcm_stellarsmiles.Constants.Constants;
import com.example.dcm_stellarsmiles.Enum.AppointmentStatus;

import java.io.Serializable;

public class Appointment implements Serializable {
    private String appointmentDate;
    private String type;
    private int cost;
    private String doctor;
    private String appointmentStatus;
    private String appointmentId;
    private String patientName;
    public Appointment()
    {}

    public Appointment(String appointmentDate, String type, String doctor) {
        this.appointmentDate = appointmentDate;
        this.type = type;
        this.doctor = doctor;
    }

    public String getAppointmentStatus() {
        return appointmentStatus;
    }

    public void setAppointmentStatus(String appointmentStatus) {
        this.appointmentStatus = appointmentStatus;
    }

    public Appointment(int cost) {
        this.cost = cost;
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

    public void setCost(int cost) {
        this.cost = cost;
    }

    public String getDoctor() {
        return doctor;
    }

    public void setDoctor(String doctor) {
        this.doctor = doctor;
    }

    public double getCost() {
        return cost;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
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
        appointment.setDoctor(this.doctor);
        appointment.setCost(this.cost);
        appointment.setAppointmentDate(this.appointmentDate);
        appointment.setType(this.type);
        return appointment;
    }
}
