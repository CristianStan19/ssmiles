package com.example.dcm_stellarsmiles.Classes.Customer;

import com.example.dcm_stellarsmiles.Classes.Appointment.Appointment;
import com.example.dcm_stellarsmiles.Enum.AppointmentStatus;
import com.example.dcm_stellarsmiles.Intefaces.CustomerAppointment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Customer implements Cloneable, Serializable {
    protected String customerID;
    protected String fullName;
    protected String email;
    protected String CNP;
    protected String phoneNumber;
    protected String birthDate;
    protected int visits = 0;
    protected boolean isSmoker;
    protected boolean isDrinker;
    protected int appoitnmentsCreated = 0;

    public Customer() {
    }

    public Customer(String fullName, String email, String phoneNumber, String birthDate, String CNP, boolean isSmoker, boolean isDrinker) {
        this.fullName = fullName;
        this.CNP = CNP;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.birthDate = birthDate;
        this.isSmoker = isSmoker;
        this.isDrinker = isDrinker;
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

    public void setAppoitnmentsCreated()
    {
        this.appoitnmentsCreated++;
    }

    public int getAppoitnmentsCreated() {
        return appoitnmentsCreated;
    }

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
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

}

