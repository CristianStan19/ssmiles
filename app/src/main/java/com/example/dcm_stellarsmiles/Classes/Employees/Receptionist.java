package com.example.dcm_stellarsmiles.Classes.Employees;

public class Receptionist extends Employee {

    public Receptionist(String name, double salary, String phoneNumber, String shift, String email) {
        super(name, salary, phoneNumber, shift, email);
        this.position = "Receptionist";
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String toString() {
        return super.toString() +
                '}';
    }
}
