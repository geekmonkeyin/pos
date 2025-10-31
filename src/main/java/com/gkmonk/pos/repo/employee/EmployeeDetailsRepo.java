package com.gkmonk.pos.repo.employee;

import com.gkmonk.pos.model.emp.EmployeeDetails;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeDetailsRepo extends MongoRepository<EmployeeDetails,String> {

}
