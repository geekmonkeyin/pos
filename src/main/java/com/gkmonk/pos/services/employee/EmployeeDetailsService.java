package com.gkmonk.pos.services.employee;

import com.gkmonk.pos.model.emp.EmployeeDetails;
import com.gkmonk.pos.repo.employee.EmployeeDetailsRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class EmployeeDetailsService {

    @Autowired
    private EmployeeDetailsRepo employeeDetailsRepo;

    public Optional<EmployeeDetails> findEmployeeById(String empId) {
        return employeeDetailsRepo.findById(empId);
    }
}
