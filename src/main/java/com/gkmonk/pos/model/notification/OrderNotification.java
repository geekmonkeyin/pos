package com.gkmonk.pos.model.notification;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;

@Document("order_notification")
@Data
public class OrderNotification {

    private String orderId;
    @Id
    private String awb;
    private String customerPhone;
    private Map<String, EventStatus> events;
    private Instant lastUpdated;
    private String nextUpdate;
    private String nextStatus;
    private String courierCompany;
    private boolean archieved;

    // Nested class for event status
    @Data
    public static class EventStatus {
        private String status;      // SENT, PENDING, etc.
        private Instant sentAt;
        private String reason;      // Optional (for failed delivery)
        private Instant attemptedAt; // Optional (for failed delivery)

        public EventStatus() {}

        }

}
