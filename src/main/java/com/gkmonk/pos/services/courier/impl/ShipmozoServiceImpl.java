package com.gkmonk.pos.services.courier.impl;

import com.gkmonk.pos.model.courier.CourierQuotationRequest;
import com.gkmonk.pos.model.order.CourierOption;
import com.gkmonk.pos.model.pod.PackedOrder;
import com.gkmonk.pos.utils.POSConstants;
import com.google.gson.JsonObject;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class ShipmozoServiceImpl extends  AbstractServiceImpl {
    @Override
    public Map<String, String> getEDD(String awb) throws Exception {
        return Map.of();
    }

    @Override
    public Map<String, String> getCurrentStatus(String awb) throws Exception {
        return Map.of();
    }

    @Override
    public JsonObject createRequestBody(String awb) {
        return null;
    }

    @Override
    public List<String> getSupportedCourierCompanies() {
        return Arrays.asList(POSConstants.DTDC_COURIER,POSConstants.BLUEDART_COURIER,POSConstants.DELHIVERY_COURIER);
    }

    @Override
    public boolean isReturnSupported() {
        return false;
    }

    @Override
    public boolean isReplacementSupported() {
        return false;
    }

    @Override
    public String createReturnOrder(PackedOrder returnOrder) {
        return "";
    }

    @Override
    public List<CourierOption> getQuote(CourierQuotationRequest courierQuotationRequest) throws Exception {
        return List.of();
    }

    @Override
    public boolean isActive() {
        return Boolean.TRUE;
    }

}
