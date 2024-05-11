package com.example.dcm_stellarsmiles.Classes.Employees;

public class Assistant extends Employee {
    private String department;

    public Assistant(String name, double salary, String phoneNumber, String email, String employeeID) {
        super(name, salary, phoneNumber, email, employeeID);
    }


    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        Assistant assistant = (Assistant) super.clone();
        assistant.setDepartment(this.department);
        return assistant;
    }

    @Override
    public String toString() {
        return super.toString() +
                "department='" + department + '\'' +
                '}';
    }
}

