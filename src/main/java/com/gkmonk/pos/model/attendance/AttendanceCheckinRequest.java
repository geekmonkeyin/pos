package com.gkmonk.pos.model.attendance;

import lombok.Data;
import lombok.extern.slf4j.XSlf4j;

@Data
public class AttendanceCheckinRequest {

    private String employeeId;
    private String employeeName;
    private String checkinIst;   // e.g. "31/10/2025, 02:35:09 pm" or ISO
    private String imageBase64;
}
