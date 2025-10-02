package com.gkmonk.pos.model.device;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("device_details")
public class DeviceDetails {

    private String deviceId;
    private String empId;
    private String empName;

}
