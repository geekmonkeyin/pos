package com.gkmonk.pos.model.returns;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gkmonk.pos.utils.ReturnStatus;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Document(collection = "return_verification_requests")
public class ReturnVerificationRequest {

    @Id
    private String returnId;
    private String orderNo;
    private Customer customer;
    private String disposition;
    private String empId;
    private String notes;
    private List<Line> lines;
    private String timestamp;
    private String videoFilename;
    private ReturnStatus returnStatus;

    @Data public static class Customer {
        private String name;
        private String phone;
    }

    @Data public static class Line {
        private String lineId;     // variantId you used in the UI
        private String productId;  // if you send it
        private String variantId;  // if you send it
        private Integer qty;
        private String marked;     // "Good" | "Damaged"
    }
}