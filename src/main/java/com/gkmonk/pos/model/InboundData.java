package com.gkmonk.pos.model;

import com.gkmonk.pos.utils.InboundStatus;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Document("inbound")
@Data
public class InboundData {

    private String vendorName;
    @Id
    private long id;
    private InboundStatus status;
    private String receivedDate;
    private Integer numberOfBoxes;
    private double totalPurchaseAmount;
    private Map<Integer,List<CartonDetails>> cartonDetails;
    private String closedBy;
}
