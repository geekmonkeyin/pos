package com.gkmonk.pos.model.notification;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum NotificationType {
    DEFAULT(0,null,Collections.singletonList("DEFAULT")),
    RETURN_DELIVERED(1,null,Arrays.asList("RETURN_DELIVERED","RTO_DELIVERED")),
    RETURN_TO_ORIGIN(2,RETURN_DELIVERED,Arrays.asList("RTO", "RETURN TO ORIGIN", "RETURN-TO-ORIGIN", "RETURN TO SELLER", "RETURN-TO-ORIGIN")),
    USER_ADDED(0,null, Collections.singletonList("USER_ADDED")),
    FEEDBACK(0,null, Collections.singletonList("FEEDBACK")),
    DELIVERED(3,FEEDBACK,Arrays.asList("DELivERED","COMPLETED")),
    OUT_FOR_DELIVERY(1,DELIVERED,Arrays.asList("OUTFORDELIVERY", "OUT FOR DELIVERY", "OUT-FOR-DELIVERY", "OUT FOR DELIVERED")),
    IN_TRANSIT(2,OUT_FOR_DELIVERY, Arrays.asList("INTRANSIT", "IN_TRANSIT", "IN-TRANSIT", "SHIPPED")),
    ORDER_PICKED(1,IN_TRANSIT,Arrays.asList("PICKED_UP", "PICKED-UP", "PICKED UP", "ORDER_PICKED", "ORDER-PICKED")),
    ORDER_PLACED(0,ORDER_PICKED,Arrays.asList("ORDER_PLACED", "ORDER-PLACED", "ORDER PLACED", "PLACED")),
    NOT_DELIVERED(1,RETURN_TO_ORIGIN,Arrays.asList("NOT_DELIVERED", "NOT-DELIVERED", "NOT DELIVERED", "NOT DELIVERED", "NOT-DELIVERED")),;

    private final int nextUpdate;
    private final NotificationType nextNotification;
    private final List<String> alternateNotification;

    NotificationType(int nextUpdate,NotificationType nextNotification, List<String> alternateNotification) {
        this.nextUpdate = nextUpdate;
        this.nextNotification = nextNotification;
        this.alternateNotification = alternateNotification;
    }

    public static NotificationType getNotifiationTypeFromText(String status) {
        for(NotificationType type : values()) {
            if (type.name().equalsIgnoreCase(status)) {
                return type;
            }
            boolean alternateMatch = type.alternateNotification.stream().anyMatch( not -> not.equalsIgnoreCase(status) );
            if(alternateMatch){
                return type;
            }
        }
        return DEFAULT;
    }

    public NotificationType getNextNotification(){
        return nextNotification;
    }

    public static NotificationType getNotificationType(String nextStatus) {
        for(NotificationType type : values()) {
            if (type.name().equalsIgnoreCase(nextStatus)) {
                return type;
            }
        }
        return null;
    }

    public String getValue() {
        return this.name();
    }

    public int getNextUpdate() {
        return nextUpdate;
    }

}
