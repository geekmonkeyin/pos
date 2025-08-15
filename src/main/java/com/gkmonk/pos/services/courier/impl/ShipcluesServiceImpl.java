package com.gkmonk.pos.services.courier.impl;

import com.gkmonk.pos.model.pod.PackedOrder;
import com.gkmonk.pos.utils.POSConstants;
import com.google.gson.JsonObject;
import com.mashape.unirest.http.HttpResponse;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ShipcluesServiceImpl  extends AbstractServiceImpl {

    @Value("${shipclues.api.url}")
    private String apiUrl;

    @Value("${shipclues.api.key}")
    private String apiKey;


    @Override
    public Map<String, String> getEDD(String awb) throws Exception {
        JsonObject requestBody = createRequestBody(awb);
        HttpResponse<String> response =  callCourierAPI(apiUrl+"/order-track",requestBody,getHeaderRequest());
        String edd= "NA";
        Map<String,String> responseMap = new HashMap<>();
        responseMap.put("edd",edd);
        return responseMap;
    }

    @Override
    public Map<String, String> getCurrentStatus(String awb) throws Exception {
        return Collections.emptyMap();
    }

    @Override
    public JsonObject createRequestBody(String awb) {
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("ApiKey", apiKey);
        requestBody.addProperty("AWBNumber", awb);
        return requestBody;
    }

    @Override
    public List<String> getSupportedCourierCompanies() {
        return Arrays.asList(POSConstants.BLUEDART_COURIER,POSConstants.DELHIVERY_COURIER,POSConstants.ECOM_COURIER);
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

    public Map<String,String> parseResponse(String responseBody) throws JSONException {

        JSONObject responseJSON = new JSONObject(responseBody);

        String courierStatus = POSConstants.EMPTY;
        Map<String,String> courierMap = new HashMap<>();

        if (!responseJSON.isNull("CurrentStatus")) {
            courierStatus = responseJSON.getString("CurrentStatus");
            courierMap.put("status",courierStatus);
        }
       // courierMap.put("edd",edd);
        return courierMap;
    }

}
