package com.gkmonk.pos.services.courier.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gkmonk.pos.model.courier.CourierQuotationRequest;
import com.gkmonk.pos.model.order.CourierOption;
import com.gkmonk.pos.model.pod.PackedOrder;
import com.gkmonk.pos.utils.POSConstants;
import com.google.gson.JsonObject;
import com.mashape.unirest.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class InstaShipServiceImpl  extends AbstractServiceImpl {

    private static final Logger log = LoggerFactory.getLogger(InstaShipServiceImpl.class);

    @Value("${instaship.api.key}")
    private String apiKey;
    private String token;
    @Value("${instaship.base.url}")
    private String baseURL;
    @Value("${instaship.booking.url}")
    private String bookingURL;
    @Value("${instaship.tracking.url}")
    private String trackingURL;
    @Value("${instaship.auth.url}")
    private String authenticationURL;


    @Override
    public Map<String, String> getEDD(String awb) throws Exception {
        String url = trackingURL + "?api_key=" + apiKey + "&airwaybilno=" + awb;
        HttpResponse<String> response =  callCourierAPIGet(url,new JsonObject(),getHeaderRequest());
         try{

             String edd = parseResponse(response.getBody());
             Map<String,String> map = new HashMap<>();
             map.put("edd",edd);
            return map;
         }catch (Exception e){
             return Collections.EMPTY_MAP;
         }
    }

    @Override
    public Map<String, String> getCurrentStatus(String awb) {
        String url = trackingURL + "?api_key=" + apiKey + "&airwaybilno=" + awb;
        try{
            HttpResponse<String> response =  callCourierAPIGet(url,new JsonObject(),getHeaderRequest());
            String status =  parseResponseForStatus(response.getBody());
            String edd = parseResponse(response.getBody());
            Map<String,String> map = new HashMap<>();
            map.put("edd",edd);
            map.put("status",status);
            return map;
        }catch (Exception e){
           log.error("Error while fetching the response from Instaship:{}",e.getMessage());
        }
        return Collections.EMPTY_MAP;

    }

    private String parseResponseForStatus(String responseBody) {

        try {
            JSONObject jsonResponse = new JSONObject(responseBody);
            JSONObject data = jsonResponse.getJSONObject("data");
            if(data != null && !data.getBoolean("error")) {
                JSONArray responseArr = data.getJSONArray("response");
                return responseArr.getJSONObject(responseArr.length()-1).getString("shipment_latest_status");

            }else{
                log.error("Error in response from Instaship: " + data.getJSONObject("response").getString("message"));
               return data.getJSONObject("response").getString("message");

            }
        } catch(Exception e) {
            log.error("Error while parsing instaship response:"+e.getMessage());
        }
        return POSConstants.EMPTY;


    }


    public void authenticate() {
        try {
            HttpResponse<String> response = callCourierAPI(authenticationURL,createRequestBody(""),getHeaderRequest());
            if (response.getStatus() == 200) {
                JSONObject responseBody = new JSONObject(response.getBody());
                token = getToken(responseBody);
            } else {
                System.out.println("Authentication failed: " + response.getStatusText());
                token = null;
            }
        } catch (Exception e) {
            System.out.println("Error during authentication: " + e.getMessage());
            token = null;
        }
    }

    @Override
    public JsonObject createRequestBody(String awb) {
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("api_key", apiKey);
        return requestBody;
    }

    public String parseResponse(String responseBody) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(responseBody);

        JsonNode responseArray = root.path("data").path("response");

        if (responseArray.isArray() && responseArray.size() > 0) {
            JsonNode firstResponse = responseArray.get(0);
            String edd = firstResponse.path("edd").asText();

            System.out.println("EDD (Estimated Delivery Date): " + edd);
            return edd;
        } else {
            System.out.println("No response data found.");
        }
        return "";

    }

    @Override
    public List<String> getSupportedCourierCompanies() {
        return Arrays.asList(POSConstants.BLUEDART_COURIER,POSConstants.DELHIVERY_COURIER);
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
        return false;
    }


    private String getToken(JSONObject responseBody) throws JSONException {
        return responseBody.getJSONObject("data")
                .getJSONObject("response")
                .getString("token_id");
    }

}
