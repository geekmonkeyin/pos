package com.gkmonk.pos.model.sorpo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Data
@Document("payments")
public class Payment {
    @Id
    private String id;

    private String storeId;
    private String poId;
    private Double amount;
    private LocalDate paymentDate;
    private String reference; // UTR, cheque no, etc.
}