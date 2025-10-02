package com.gkmonk.pos.model.emp;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("employee_details")
public class EmployeeDetails {

    private String empName;
    private String empId;
    private String doj;
    private double salary;
    private String designation;
    private String department;
    private String location;
    private String contactNumber;
    private String email;
    private String address;
    private String emergencyContact;
    private String aadharNumber;
    private String panNumber;



}
