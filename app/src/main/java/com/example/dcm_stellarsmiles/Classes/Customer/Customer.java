package com.example.dcm_stellarsmiles.Classes.Customer;

import com.example.dcm_stellarsmiles.Enum.AppointmentStatus;
import com.example.dcm_stellarsmiles.Intefaces.CustomerAppointment;

public class Customer implements Cloneable, CustomerAppointment {
    protected String fullName;
    protected String email;
    protected String CNP;
    protected String phoneNumber;
    protected String birthDate;
    protected int visits = 0;
    protected boolean isSmoker;
    protected boolean isDrinker;
    protected AppointmentStatus appointmentStatus = AppointmentStatus.NO_APPOINTMENT;

    public Customer(String fullName, String email, String phoneNumber, String birthDate, String CNP, boolean isSmoker, boolean isDrinker) {
        this.fullName = fullName;
        this.CNP = CNP;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.birthDate = birthDate;
        this.isSmoker = isSmoker;
        this.isDrinker = isDrinker;
    }

    public AppointmentStatus getAppointmentStatus() {
        return appointmentStatus;
    }

    public void setAppointmentStatus(AppointmentStatus appointmentStatus) {
        this.appointmentStatus = appointmentStatus;
    }

    public int getVisits() {
        return visits;
    }

    public void setVisits(int visits) {
        this.visits = visits;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getCNP() {
        return CNP;
    }

    public void setCNP(String CNP) {
        this.CNP = CNP;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public boolean isSmoker() {
        return isSmoker;
    }

    public void setSmoker(boolean smoker) {
        isSmoker = smoker;
    }

    public boolean isDrinker() {
        return isDrinker;
    }

    public void setDrinker(boolean drinker) {
        isDrinker = drinker;
    }


    @Override
    public String toString() {
        return "Customer{" +
                "fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", CNP='" + CNP + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", birthDate='" + birthDate + '\'' +
                ", visits=" + visits +
                ", isSmoker=" + isSmoker +
                ", isDrinker=" + isDrinker +
                '}';
    }



    @Override
    public Object clone() throws CloneNotSupportedException {
        Customer customer = (Customer) super.clone();
        customer.setVisits(this.visits);
        customer.setCNP(this.CNP);
        customer.setEmail(this.email);
        customer.setPhoneNumber(this.phoneNumber);
        customer.setFullName(this.fullName);
        customer.setBirthDate(this.birthDate);
        customer.setDrinker(this.isDrinker);
        customer.setSmoker(this.isSmoker);
        return customer;
    }

    @Override
    public void cancelAppointment() {
        appointmentStatus = AppointmentStatus.CANCELED_APPOINTMENT;
        System.out.println("Your appointment has been canceled!");
    }

    @Override
    public void checkAppointment() {
        String status;
        switch (appointmentStatus) {
            case NO_APPOINTMENT:
                status = "you have no appointment.";
                break;
            case CANCELED_APPOINTMENT:
                status = "canceled.";
                break;
            case APPOINTMENT_ONGOING:
                status = "ongoing";
                break;
            default:
                status = "unknown";
                break;
        }
        System.out.println("Your appointment status is " + status);
    }
}

