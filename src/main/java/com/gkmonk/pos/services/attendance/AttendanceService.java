package com.gkmonk.pos.services.attendance;

import com.gkmonk.pos.model.attendance.AttendanceDTO;
import com.gkmonk.pos.model.attendance.AttendanceSession;
import com.gkmonk.pos.repo.attendance.AttendanceRepo;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.ByteArrayInputStream;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;


@Service
public class AttendanceService {

    private static final ZoneId IST = ZoneId.of("Asia/Kolkata");
    private static final DateTimeFormatter IST_FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy, hh:mm:ss a")
                    .withLocale(Locale.ENGLISH)
                    .withZone(IST); // format Instant -> IST

    @Autowired private AttendanceRepo repo;
    @Autowired private GridFsTemplate gridFs;

    public boolean hasOpenSession(String employeeId) {
        return repo.existsByEmployeeIdAndCheckoutAtIsNull(employeeId);
    }

    public AttendanceSession updateCheckoutTime(String sessionId, Instant checkoutAt,  byte[] imageBytes,
                                                String contentType) {
        Assert.hasText(sessionId, "sessionId required");
        Assert.notNull(checkoutAt, "checkoutAt required");

        AttendanceSession s = repo.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid sessionId: " + sessionId));
        s.setCheckoutAt(checkoutAt);
        return repo.save(s);

    }

    public AttendanceSession saveCheckin(String employeeId,
                                         String employeeName,
                                         Instant checkinAt,
                                         byte[] imageBytes,
                                         String contentType) {
        Assert.hasText(employeeId, "employeeId required");
        Assert.hasText(employeeName, "employeeName required");
        Assert.notNull(checkinAt, "checkinAt required");

        AttendanceSession s = new AttendanceSession();
        s.setEmployeeId(employeeId);
        s.setEmployeeName(employeeName);
        s.setCheckinAt(checkinAt);

        if (imageBytes != null && imageBytes.length > 0) {
            GridFSUploadOptions opts = new GridFSUploadOptions()
                    .metadata(new org.bson.Document("contentType", contentType != null ? contentType : "image/png")
                            .append("employeeId", employeeId)
                            .append("type", "checkin"));
            ObjectId fileId = gridFs.store(
                    new ByteArrayInputStream(imageBytes),
                    "attn-" + employeeId + "-" + System.currentTimeMillis() + ".png",
                    opts
            );
            s.setCheckinPhotoGridFsId(fileId);
        }
        return repo.save(s);
    }

    /**
     * Fetch all sessions where check-in OR check-out falls within today's IST window.
     * Formats timestamps for the UI and returns a lightweight DTO list.
     */
    public List<AttendanceDTO> getTodayAttendance() {
        // Compute IST day window [today 00:00, tomorrow 00:00)
        LocalDate todayIst = LocalDate.now(IST);
        Instant start = todayIst.atStartOfDay(IST).toInstant();
        Instant end   = todayIst.plusDays(1).atStartOfDay(IST).toInstant();

        // Query: any record with checkinAt in window OR checkoutAt in window
        List<AttendanceSession> sessions =
                repo.findTodaysRecord(start, end);

        // Map to DTO with UI-friendly IST strings (lower-case am/pm to match frontend parser)
        return sessions.stream().map(s -> {
            AttendanceDTO dto = new AttendanceDTO();
            dto.setSessionId(s.getId());
            dto.setEmployeeId(s.getEmployeeId());
            dto.setEmployeeName(s.getEmployeeName());
            //dto.setPhotoUrl();
            if (s.getCheckinAt() != null) {
                dto.setCheckinIst(IST_FMT.format(s.getCheckinAt()).toLowerCase(Locale.ENGLISH));
            }
            if (s.getCheckoutAt() != null) {
                dto.setCheckoutIst(IST_FMT.format(s.getCheckoutAt()).toLowerCase(Locale.ENGLISH));
            }

            // If you already persist/compute a photo URL elsewhere, set it here.
            // Leaving null lets the frontend fall back to /v1/attendance/photo/latest or employee details.
            dto.setPhotoUrl(null);

            return dto;
        }).collect(Collectors.toList());
    }
}
