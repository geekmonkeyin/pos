package com.gkmonk.pos.services.courier.impl;

import com.gkmonk.pos.model.courier.CourierQuotationRequest;
import com.gkmonk.pos.model.order.CourierOption;
import com.gkmonk.pos.model.pod.PackedOrder;
import com.gkmonk.pos.utils.POSConstants;
import com.google.gson.JsonObject;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
public class DTDCServiceImpl extends AbstractServiceImpl {

    private static final Logger log = LoggerFactory.getLogger(DTDCServiceImpl.class);

    @Value("${dtdc.tracking.url}")
    private String dtdcTrackingURL;

    @Value("${dtdc.authenticate.url}")
    private String dtdcAuthURL;

    @Value("${dtdc.username}")
    private String userName;

    @Value("${dtdc.password}")
    private String password;

    private String dtdcToken;

    @Override
    public Map<String, String> getEDD(String awb) throws Exception {
        return fetchCourierData(awb, dtdcTrackingURL, createRequestBody(awb), body -> {
            String edd = parseEDD(body);
            Map<String,String> map = new HashMap<>();
            map.put("edd",edd);
            return map;
        });
    }

    @Override
    public Map<String, String> getCurrentStatus(String awb) throws Exception {
        return fetchCourierData(awb, dtdcTrackingURL, createRequestBody(awb), this::parseCurrentStatus);
    }

    private String parseEDD(String responseBody) {
        try {
            JSONObject response = new JSONObject(responseBody);
            JSONObject trackHeader = response.optJSONObject("trackHeader");
            return trackHeader != null ? trackHeader.optString("strExpectedDeliveryDate", "") : "";
        } catch (JSONException e) {
            log.error("Error parsing EDD response: {}", e.getMessage());
            return POSConstants.EMPTY;
        }
    }

    private Map<String, String> parseCurrentStatus(String responseBody) {
        Map<String, String> result = new HashMap<>();
        try {
            JSONObject responseJSON = new JSONObject(responseBody);
            if ("success".equalsIgnoreCase(responseJSON.optString("status"))) {
                JSONArray trackDetails = responseJSON.optJSONArray("trackDetails");
                if (trackDetails != null && trackDetails.length() > 0) {
                    JSONObject latestStatus = trackDetails.getJSONObject(trackDetails.length() - 1);
                    result.put("currentStatus", latestStatus.optString("strAction", "No Status Available"));
                }
                result.put("edd", parseEDD(responseBody));
            } else {
                log.info("DTDC Response Error: {}", responseJSON.optString("message"));
            }
        } catch (JSONException e) {
            log.error("Error parsing current status response: {}", e.getMessage());
        }
        return result.isEmpty() ? Collections.emptyMap() : result;
    }

    private String getAccessToken() {
        try {
            HttpResponse<String> response = Unirest.get(dtdcAuthURL)
                    .queryString("username", userName)
                    .queryString("password", password)
                    .asString();

            if (response.getStatus() == 200) {
                return response.getBody();
            } else {
                log.error("Failed to fetch DTDC token. Status: {}, Message: {}", response.getStatus(), response.getStatusText());
            }
        } catch (UnirestException e) {
            log.error("Error while fetching DTDC token: {}", e.getMessage());
        }
        return null;
    }

    @Override
    public JsonObject createRequestBody(String awb) {
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("trkType", "cnno");
        requestBody.addProperty("strcnno", awb);
        requestBody.addProperty("addtnlDtl", "Y");
        return requestBody;
    }

    @Override
    public Map<String, String> getHeaderRequest() {
        Map<String, String> headers = super.getHeaderRequest();
        headers.put("X-Access-Token", dtdcToken);
        return headers;
    }

    @Override
    public List<String> getSupportedCourierCompanies() {
        return Collections.singletonList(POSConstants.DTDC_COURIER);
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

    private <T> T fetchCourierData(String awb, String url, JsonObject requestBody, Function<String, T> responseParser) throws Exception {
        if (dtdcToken == null) {
            dtdcToken = getAccessToken();
        }
        if (dtdcToken == null) {
            throw new Exception("Failed to authenticate with DTDC service.");
        }
        try {
            HttpResponse<String> response = callCourierAPI(url, requestBody, getHeaderRequest());
            if (response.getStatus() == 200) {
                return responseParser.apply(response.getBody());
            } else {
                log.error("API call failed. Status: {}, Message: {}", response.getStatus(), response.getStatusText());
                throw new Exception("Failed to fetch courier data. Status: " + response.getStatus());
            }
        } catch (Exception e) {
            log.error("Error during API call for AWB {}: {}", awb, e.getMessage());
            throw new Exception("Error fetching courier data for AWB: " + awb, e);
        }
    }
}