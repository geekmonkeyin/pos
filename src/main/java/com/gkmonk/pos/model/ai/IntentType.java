package com.gkmonk.pos.model.ai;

public enum IntentType {

    ORDER_STATUS("order_status"),
    ORDER_PLACEMENT("order_placement"),
    ORDER_CANCELLATION("order_cancellation"),
    ORDER_RETURN("order_return"),
    ORDER_REFUND("order_refund"),
    PRODUCT_INQUIRY("product_inquiry"),
    SHIPPING_INQUIRY("shipping_inquiry"),
    PAYMENT_INQUIRY("payment_inquiry"),
    CUSTOMER_SUPPORT("customer_support"),
    FEEDBACK("feedback");

    private final String intentName;

    IntentType(String intentName) {
        this.intentName = intentName;
    }

    public String getIntentName() {
        return intentName;
    }

}
