package com.example.dcm_stellarsmiles.Classes.Employees;

import com.example.dcm_stellarsmiles.Constants.Constants;

public abstract class Employee implements Cloneable {
    protected String name;
    protected String employeeID;
    protected double salary;
    protected String position = Constants.DEFAULT_POSITION;
    protected String phoneNumber;
    protected String email;


    public Employee(String name, double salary, String phoneNumber, String email, String employeeID) {
        this.name = name;
        this.employeeID = employeeID;
        this.salary = salary;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmployeeID() {
        return employeeID;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
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

    public void setEmployeeID(String employeeID)
    {
        this.employeeID = employeeID;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "name='" + name + '\'' +
                ", employeeID='" + employeeID + '\'' +
                ", salary=" + salary +
                ", position='" + position + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' ;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        Employee employee = (Employee) super.clone();
        employee.setSalary(this.salary);
        employee.setEmployeeID(this.employeeID);
        employee.setName(this.name);
        return employee;
    }

}