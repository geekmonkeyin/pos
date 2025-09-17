package com.gkmonk.pos.model;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "gst_rates")
@Data
public class GSTRate {

    private String category;
    private String hsn;
    private double rate;

}
