package com.gkmonk.pos.model.packaging;


import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class PackagingFilter {
        // date filter (BSON Date)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        private LocalDate fromDate;
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        private LocalDate toDate;
        // If your DB stores date as string, specify the Mongo format, e.g. "%d-%m-%Y"
        private String dateFormat; // optional
        private String search;     // orderNo/customerName/pincode contains
        private String courier;
        private String warehouse;
        private String status;
        private Integer divisor = 5000; // 5000 or 6000
        private Double minCw;           // min chargeable kg
        private Double maxCw;           // max chargeable kg
        private Integer page = 0;       // zero-based
        private Integer size = 50;      // page size
        private Boolean hasActual;      // only those with actualWeight present
        private Boolean missingDims;    // only those missing any dimension

}
