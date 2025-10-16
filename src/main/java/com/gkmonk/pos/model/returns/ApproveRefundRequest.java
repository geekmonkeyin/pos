package com.gkmonk.pos.model.returns;

import lombok.Data;

@Data
public class ApproveRefundRequest {
    private String returnId;   // required
    private String orderNo;    // optional (for extra validation / logging)
    private String note;       // optional
    private String refundedReference;
}