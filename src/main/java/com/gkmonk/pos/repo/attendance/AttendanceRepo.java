package com.gkmonk.pos.repo.attendance;

import com.gkmonk.pos.model.attendance.AttendanceDTO;
import com.gkmonk.pos.model.attendance.AttendanceSession;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.Instant;
import java.util.List;

public interface AttendanceRepo extends MongoRepository<AttendanceSession, String> {

    boolean existsByEmployeeIdAndCheckoutAtIsNull(String employeeId);

    @Query("{'checkinAt':{$gt:?0,$lt:?1}}"
    )
    List<AttendanceSession> findTodaysRecord(Instant start1, Instant end1);

}
