package com.example.dcm_stellarsmiles.Classes.Employees;

public class Assistant extends Employee {
    private String department;
    public Assistant(){}
    public Assistant(String name, String phoneNumber, String email, String employeeID) {
        super(name, phoneNumber, email, employeeID);
        position = "Assistant";
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

