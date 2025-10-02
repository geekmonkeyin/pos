package com.gkmonk.pos.utils;

public enum TaskPoints {

    PRODUCT_POINT(1),ORDER_POINT(1),OUTBOUND_POINT(1),INBOUND_POINT(1),RETURN_POINT(1);

    public int points;

    TaskPoints(int points) {
        this.points = points;
    }



}
