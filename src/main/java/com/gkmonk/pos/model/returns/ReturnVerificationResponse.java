package com.gkmonk.pos.model.returns;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor(staticName = "ok")
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReturnVerificationResponse {

    private boolean success;
    private String message;
    private String orderNo;
    private Long imageCount;
    private Long videoSize;

    public static ReturnVerificationResponse error(String msg) {
        ReturnVerificationResponse r = new ReturnVerificationResponse();
        r.success = false;
        r.message = msg;
        return r;
    }

}
