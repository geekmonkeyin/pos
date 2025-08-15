package com.gkmonk.pos.model.logs;

public enum TaskType {
    CREATE_ORDER,
    UPDATE_ORDER,
    DELETE_ORDER,
    CREATE_SHIPMENT,
    UPDATE_SHIPMENT,
    DELETE_SHIPMENT,
    CREATE_INVOICE,
    UPDATE_INVOICE,
    DELETE_INVOICE,
    CREATE_PAYMENT,
    UPDATE_PAYMENT,
    DELETE_PAYMENT,
    CREATE_CUSTOMER,
    UPDATE_CUSTOMER,
    DELETE_CUSTOMER,
    CREATE_PRODUCT,
    UPDATE_PRODUCT,
    DELETE_PRODUCT,
    CREATE_COUPON,
    UPDATE_COUPON,
    DELETE_COUPON,
    UPDATE_STOCK,
    UPDATE_INVENTORY,
    CREATE_NOT_LISTED_ENTRY, INBOUND_INVENTORY, BARCODE_GENERATION, OUTBOUND_ORDER;

    public static TaskType getTaskTypeByName(String name) {
        for (TaskType taskType : TaskType.values()) {
            if (taskType.name().equalsIgnoreCase(name)) {
                return taskType;
            }
        }
        return null;
    }
}
