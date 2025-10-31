package com.gkmonk.pos.services.attendance;

import com.gkmonk.pos.model.attendance.AttendanceSession;
import com.gkmonk.pos.repo.attendance.AttendanceRepo;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import lombok.extern.slf4j.XSlf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.ByteArrayInputStream;
import java.time.Instant;

@Service
public class AttendanceService {

    @Autowired
    private AttendanceRepo repo;
    @Autowired
    private GridFsTemplate gridFs;


    public boolean hasOpenSession(String employeeId) {
        return repo.existsByEmployeeIdAndCheckoutAtIsNull(employeeId);
    }

    public AttendanceSession saveCheckin(String employeeId,
                                         String employeeName,
                                         Instant checkinAt,
                                         byte[] imageBytes,
                                         String contentType) {
        Assert.hasText(employeeId, "employeeId required");
        Assert.hasText(employeeName, "employeeName required");

        AttendanceSession s = new AttendanceSession();
        s.setEmployeeId(employeeId);
        s.setEmployeeName(employeeName);
        s.setCheckinAt(checkinAt);

        if (imageBytes != null && imageBytes.length > 0) {
            GridFSUploadOptions opts = new GridFSUploadOptions()
                    .metadata(new org.bson.Document("contentType", contentType != null ? contentType : "image/png")
                            .append("employeeId", employeeId)
                            .append("type", "checkin"));
            ObjectId fileId = gridFs.store(new ByteArrayInputStream(imageBytes),
                    "attn-" + employeeId + "-" + System.currentTimeMillis() + ".png",
                    opts);
            s.setPhotoGridFsId(fileId);
        }

        return repo.save(s);
    }
}
