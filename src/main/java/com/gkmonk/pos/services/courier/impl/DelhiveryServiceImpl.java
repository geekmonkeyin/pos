//package com.gkmonk.pos.services.courier.impl;
//
//import com.gkmonk.pos.model.pod.PackedOrder;
//import com.gkmonk.pos.utils.POSConstants;
//import com.google.gson.JsonObject;
//import org.springframework.stereotype.Service;
//
//import java.util.Collections;
//import java.util.List;
//import java.util.Map;
//
//@Service
//public class DelhiveryServiceImpl extends  AbstractServiceImpl{
//    @Override
//    public Map<String, String> getEDD(String awb) throws Exception {
//        return Map.of();
//    }
//
//    @Override
//    public Map<String, String> getCurrentStatus(String awb) throws Exception {
//        return Map.of();
//    }
//
//    @Override
//    public JsonObject createRequestBody(String awb) {
//        return null;
//    }
//
//    @Override
//    public List<String> getSupportedCourierCompanies() {
//        return Collections.singletonList(POSConstants.DELHIVERY_COURIER);
//    }
//
//    @Override
//    public boolean isReturnSupported() {
//        return true;
//    }
//
//    @Override
//    public boolean isReplacementSupported() {
//        return true;
//    }
//
//    @Override
//    public String createReturnOrder(PackedOrder returnOrder) {
//        return "";
//    }
//}
