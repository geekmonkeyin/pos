package com.gkmonk.pos.services.courier;

import com.gkmonk.pos.model.pod.PackedOrder;
import com.google.gson.JsonObject;

import java.util.List;
import java.util.Map;

public interface IOrdersSyncService {


    Map<String, String> getEDD(String awb)throws Exception;

    Map<String, String> getCurrentStatus(String awb) throws Exception;

    JsonObject createRequestBody(String awb);

    Map<String,String> getHeaderRequest();

    List<String> getSupportedCourierCompanies();

    boolean isReturnSupported();

    boolean isReplacementSupported();

    String createReturnOrder(PackedOrder returnOrder);
}
