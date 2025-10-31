package com.gkmonk.pos.model.attendance;

import lombok.Data;

@Data
public class AttendanceCheckinResponse {
    private String sessionId;
    private String employeeId;
    private String employeeName;
    private String checkinIst; // ISO-8601 in IST zone for readability
    private String photoUrl;

    public AttendanceCheckinResponse(String id, String employeeId, String employeeName, String string, String photoUrl) {
        this.sessionId = id;
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.checkinIst = string;
        this.photoUrl = photoUrl;
    }
}
