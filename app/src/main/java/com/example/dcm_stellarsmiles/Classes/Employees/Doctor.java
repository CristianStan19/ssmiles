package com.example.dcm_stellarsmiles.Classes.Employees;

import com.example.dcm_stellarsmiles.Constants.Constants;

public class Doctor extends Employee {
    private String specialization;

    public Doctor(String name, double salary, String phoneNumber, String email, String employeeID) {
        super(name, salary, phoneNumber, email, employeeID);
        this.position = "Doctor";
    }


    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        Doctor doctor = (Doctor) super.clone();
        doctor.setSpecialization(this.specialization);
        return doctor;
    }

    @Override
    public String toString() {
        return super.toString() +
                "specialization='" + specialization + '\'' +
                '}';
    }
}