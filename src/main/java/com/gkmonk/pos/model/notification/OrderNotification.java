package com.gkmonk.pos.model.notification;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;

@Document("order_notification")
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



    public OrderNotification() {}

    // Getters and Setters

    public String getCourierCompany() {
        return courierCompany;
    }
    public void setCourierCompany(String courierCompany) {
        this.courierCompany = courierCompany;
    }
    public String getAwb() {
        return awb;
    }
    public void setAwb(String awb) {
        this.awb =  awb;
    }
    public String getNextStatus() {
        return nextStatus;
    }
    public void setNextStatus(String nextStatus) {
        this.nextStatus = nextStatus;
    }
    public String getNextUpdate() {
        return nextUpdate;
    }
    public void setNextUpdate(String nextUpdate) {
        this.nextUpdate = nextUpdate;
    }
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public Map<String, EventStatus> getEvents() {
        return events;
    }

    public void setEvents(Map<String, EventStatus> events) {
        this.events = events;
    }

    public Instant getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Instant lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    // Nested class for event status
    public static class EventStatus {
        private String status;      // SENT, PENDING, etc.
        private Instant sentAt;
        private String reason;      // Optional (for failed delivery)
        private Instant attemptedAt; // Optional (for failed delivery)

        public EventStatus() {}

        // Getters and Setters
        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Instant getSentAt() {
            return sentAt;
        }

        public void setSentAt(Instant sentAt) {
            this.sentAt = sentAt;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }

        public Instant getAttemptedAt() {
            return attemptedAt;
        }

        public void setAttemptedAt(Instant attemptedAt) {
            this.attemptedAt = attemptedAt;
        }
    }
    public boolean isArchieved() {
        return archieved;
    }
    public void setArchieved(boolean archieved) {
        this.archieved = archieved;
    }
}
