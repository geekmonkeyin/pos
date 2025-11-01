package com.gkmonk.pos.model.attendance;

import lombok.Data;

@Data
public class  AttendanceCheckinRequest {

    private String employeeId;
    private String employeeName;
    private String checkinIst;
    private String checkoutIst; // e.g. "31/10/2025, 02:35:09 pm" or ISO
    private String imageBase64;
    private String sessionId;
}
