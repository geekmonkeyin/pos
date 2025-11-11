package com.gkmonk.pos.model.courier;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourierQuotationRequest {

    private String order_id;
    private String delivery_pincode;
    private String pickup_pincode;
    private String payment_type;
    private String shipment_type;
    private double order_amount;
    private String type_of_package;
    private String rov_type;
    private double cod_amount;
    private double weight;
    private List<Dimension> dimensions;

    @Data
    static class Dimension{

        private int no_of_box = 1;
        private double length;
        private double width;
        private double height;
        private double dead_weight_kg;
    }

}
