package com.gkmonk.pos.utils;

public class OrderUtils {

    public static final double DEFAULT_WEIGHT = 0.5;
    public static final double DEFAULT_WEIGHT_2KG = 2.0;
    public static final double DEFAULT_WEIGHT_5KG = 5.0;
    public static final double DEFAULT_WEIGHT_10KG = 10.0;


    public static double getLength(double weight) {
        if(DEFAULT_WEIGHT_2KG == weight){
            return 21.5d;
        }
        if(DEFAULT_WEIGHT_5KG == weight){
            return 29.2d;
        }
        if(DEFAULT_WEIGHT_10KG == weight){
            return 36.75d;
        }


        return 10d;

    }
}
