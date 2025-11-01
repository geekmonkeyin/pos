package com.gkmonk.pos.model.attendance;

import lombok.Data;

@Data
public class AttendanceDTO {
    private String sessionId;
    private String employeeId;
    private String employeeName;
    private String checkinIst;
    private String checkoutIst;// ISO-8601 in IST zone for readability
    private String photoUrl;

    public AttendanceDTO() {
    }
    public AttendanceDTO(String id, String employeeId, String employeeName, String checkin, String photoUrl) {
        this.sessionId = id;
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.checkinIst = checkin;
        this.photoUrl = photoUrl;
    }
}
