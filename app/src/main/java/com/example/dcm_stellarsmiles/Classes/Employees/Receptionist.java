package com.example.dcm_stellarsmiles.Classes.Employees;

public class Receptionist extends Employee {
    public Receptionist(){}

    public Receptionist(String name, String phoneNumber, String shift, String email) {
        super(name, phoneNumber, shift, email);
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
