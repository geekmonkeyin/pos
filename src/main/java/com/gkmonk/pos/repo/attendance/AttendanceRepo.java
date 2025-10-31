package com.gkmonk.pos.repo.attendance;

import com.gkmonk.pos.model.attendance.AttendanceSession;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AttendanceRepo extends MongoRepository<AttendanceSession, String> {

    boolean existsByEmployeeIdAndCheckoutAtIsNull(String employeeId);

}
