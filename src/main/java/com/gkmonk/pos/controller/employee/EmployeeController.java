package com.gkmonk.pos.controller.employee;

import com.gkmonk.pos.model.emp.EmployeeDetails;
import com.gkmonk.pos.services.employee.EmployeeDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/v1/employees")
public class EmployeeController {

    @Autowired
    private EmployeeDetailsService employeeDetailsService;

    @GetMapping("/search/{id}")
    public ResponseEntity<EmployeeDetails> searchEmployeeById(@PathVariable("id") String id) {
        // Implementation to search employee by ID
        Optional<EmployeeDetails> employeeDetails = employeeDetailsService.findEmployeeById(id);
        return employeeDetails.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.ok(new EmployeeDetails()));
    }


}