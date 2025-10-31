package com.gkmonk.pos.model.attendance;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document("attendance_sessions")
@Data
public class AttendanceSession {

        @Id
        private String id;
        private String employeeId;
        private String employeeName;
        private Instant checkinAt;   // stored as UTC instant
        private Instant checkoutAt;  // null until checkout
        private ObjectId photoGridFsId; // optional

    }
