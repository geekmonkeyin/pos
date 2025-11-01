package com.gkmonk.pos.controller.attendance;

import com.gkmonk.pos.controller.exceptions.ConflictException;
import com.gkmonk.pos.model.attendance.AttendanceCheckinRequest;
import com.gkmonk.pos.model.attendance.AttendanceDTO;
import com.gkmonk.pos.model.attendance.AttendanceSession;
import com.gkmonk.pos.services.attendance.AttendanceService;
import com.gkmonk.pos.utils.IstParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/v1/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceService service;

    @GetMapping
    public ModelAndView getAttendancePage() {

        ModelAndView modelAndView = new ModelAndView("attendance");
        return modelAndView;
    }

    @GetMapping("/today")
    public ResponseEntity<List<AttendanceDTO>> getToday() {
        return ResponseEntity.ok(service.getTodayAttendance());
    }



    @PostMapping("/checkin")
    public ResponseEntity<AttendanceDTO> checkin(@RequestBody AttendanceCheckinRequest req) {

        if (!StringUtils.hasText(req.getEmployeeId()) || !StringUtils.hasText(req.getEmployeeName())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        // Parse IST timestamp
        Instant checkinInstant = IstParser.parseIstToInstant(req.getCheckinIst());

        // Decode image
        byte[] imageBytes = null;
        String contentType = "image/png";
        if (StringUtils.hasText(req.getImageBase64())) {
            // accept data URLs like "data:image/png;base64,AAAA..."
            String b64 = req.getImageBase64().trim();
            int comma = b64.indexOf(',');
            if (b64.startsWith("data:") && comma > 0) {
                String meta = b64.substring(5, comma); // e.g., "image/png;base64"
                if (meta.startsWith("image/")) {
                    contentType = meta.substring(0, meta.indexOf(';'));
                }
                b64 = b64.substring(comma + 1);
            }
            imageBytes = Base64.getDecoder().decode(b64);
        }

        // Enforce business rule (no new check-in if open session exists)
        if (service.hasOpenSession(req.getEmployeeId())) {
            throw new ConflictException("Open session exists. Please check out first.");
        }

        // Persist (session + photo in GridFS)
        AttendanceSession saved = service.saveCheckin(
                req.getEmployeeId(),
                req.getEmployeeName(),
                checkinInstant,
                imageBytes,
                contentType
        );

        // Build a photo URL that matches your earlier UI (latest photo endpoint)
        String encodedId = URLEncoder.encode(req.getEmployeeId(), StandardCharsets.UTF_8);
        String photoUrl = "/v1/attendance/photo/latest?employeeId=" + encodedId;

        AttendanceDTO resp = new AttendanceDTO(
                saved.getId(),
                req.getEmployeeId(),
                req.getEmployeeName(),
                checkinInstant.atZone(ZoneId.of("Asia/Kolkata")).toString(),
                photoUrl
        );
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(@RequestBody AttendanceCheckinRequest req) {
        try {
            if (req.getEmployeeId() == null || req.getEmployeeId().isEmpty()) {
                return ResponseEntity.badRequest().body("Missing employeeId");
            }

            // Parse IST timestamp from frontend string
            Instant checkoutAt = IstParser.parseIstToInstant(req.getCheckoutIst());

            // Convert Base64 image if present
            byte[] imageBytes = null;
            String contentType = "image/png";
            if (req.getImageBase64() != null && req.getImageBase64().startsWith("data:")) {
                // Format: data:image/png;base64,XXXXX
                int commaIdx = req.getImageBase64().indexOf(',');
                if (commaIdx > 0) {
                    String meta = req.getImageBase64().substring(5, commaIdx); // image/png;base64
                    if (meta.contains(";")) contentType = meta.substring(0, meta.indexOf(';'));
                    imageBytes = Base64.getDecoder().decode(req.getImageBase64().substring(commaIdx + 1));
                }
            }
            AttendanceSession updated = service.updateCheckoutTime(
                    req.getSessionId(),
                    checkoutAt,
                    imageBytes,
                    contentType
            );

            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Checkout failed: " + e.getMessage());
        }

    }
}
