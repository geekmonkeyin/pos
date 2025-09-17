package com.gkmonk.pos.model.returns;

import com.gkmonk.pos.model.pod.PackedOrder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("return_order")
@Data
public class ReturnOrder {

    @Id
    private String orderId;
    private PackedOrder packedOrder;
    private String labelLink;

    public ReturnOrder() {
    }

}
