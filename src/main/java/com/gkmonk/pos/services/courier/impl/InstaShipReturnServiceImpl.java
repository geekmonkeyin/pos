package com.gkmonk.pos.services.courier.impl;

import com.gkmonk.pos.model.pod.CustomerInfo;
import com.gkmonk.pos.model.pod.PackedOrder;
import com.gkmonk.pos.model.pod.ProductDetails;
import com.gkmonk.pos.utils.MapperUtils;
import com.gkmonk.pos.utils.OrderUtils;
import com.gkmonk.pos.utils.POSConstants;
import com.gkmonk.pos.utils.PhoneUtils;
import com.gkmonk.pos.utils.StringUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mashape.unirest.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class InstaShipReturnServiceImpl extends AbstractServiceImpl {

    private static final Logger log = LoggerFactory.getLogger(InstaShipReturnServiceImpl.class);

    @Value("${instaship.api.url}")
    private String apiUrl;

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
    public Map<String, String> getEDD(String awb) {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, String> getCurrentStatus(String awb) {
        return Collections.emptyMap();
    }

    @Override
    public JsonObject createRequestBody(String awb) {
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("api_key", apiKey);
        return requestBody;
    }

    @Override
    public List<String> getSupportedCourierCompanies() {
        return Arrays.asList(POSConstants.BLUEDART_COURIER, POSConstants.DELHIVERY_COURIER);
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
        return bookShipment(returnOrder);
    }

    public JsonObject createReturnRequestBody(PackedOrder packedOrder) {
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("token_id", token);
        requestBody.addProperty("auto_approve", true);
        requestBody.addProperty("order_number", packedOrder.getOrderId());
        requestBody.addProperty("transaction_ref_no", packedOrder.getOrderId());

        double weight = 0.5d;
        updatePaymentInfo(requestBody, packedOrder);
        updateWeightInfo(requestBody, weight);
        requestBody.add("shipping", getShippingDetails());
        requestBody.add("line_items", getLineItems(packedOrder.getProductDetails()));
        requestBody.add("pickup", getPickupAddress(packedOrder.getCustomerInfo()));
        requestBody.add("rto", MapperUtils.getPickupDetails());
        requestBody.add("gst_details", MapperUtils.getGSTDetails());

        return requestBody;
    }

    private JsonObject getPickupAddress(CustomerInfo pickupDetails) {
        JsonObject pickup = new JsonObject();
        pickup.addProperty("vendor_name", "GIAN Retails Pvt Ltd");
        pickup.addProperty("address_1", pickupDetails.getAddress());
        pickup.addProperty("address_2", StringUtils.defaultIfBlank(pickupDetails.getCity(), POSConstants.EMPTY));
        pickup.addProperty("city", pickupDetails.getCity());
        pickup.addProperty("state", pickupDetails.getState());
        pickup.addProperty("postcode", pickupDetails.getPostalCode());
        pickup.addProperty("country", pickupDetails.getCountry());
        pickup.addProperty("phone", PhoneUtils.formatPhoneNo(pickupDetails.getPhoneNo()));
        return pickup;
    }

    private JsonObject getShippingDetails() {
        JsonObject shipping = new JsonObject();
        shipping.addProperty("first_name", "GIAN Retails Pvt Ltd");
        shipping.addProperty("last_name", "");
        shipping.addProperty("address_1", "Geekmonkey warehouse, 54/10 Rajpur Road, Near Pantaloons Showroom");
        shipping.addProperty("address_2", "Adjacent to Ram Sharanam Ashram");
        shipping.addProperty("city", "Dehradun");
        shipping.addProperty("state", "Uttarakhand");
        shipping.addProperty("postcode", "248001");
        shipping.addProperty("country", "India");
        shipping.addProperty("phone", "9560772223");
        shipping.addProperty("cust_email", "geekmonkeyin@gmail.com");
        return shipping;
    }

    private void updateWeightInfo(JsonObject requestBody, double weight) {
        requestBody.addProperty("length", OrderUtils.getLength(weight));
        requestBody.addProperty("breadth", OrderUtils.getLength(weight));
        requestBody.addProperty("height", OrderUtils.getLength(weight));
        requestBody.addProperty("actual_weight", weight);
        requestBody.addProperty("volumetric_weight", weight);
    }

    private void updatePaymentInfo(JsonObject requestBody, PackedOrder packedOrder) {
        requestBody.addProperty("payment_method", "PPD");
        requestBody.addProperty("discount_total", "0.00");
        requestBody.addProperty("cod_shipping_charge", "00.00");
        requestBody.addProperty("invoice_total", 2000d);
        requestBody.addProperty("cod_total", 0.0);
    }

    private JsonArray getLineItems(List<ProductDetails> productDetails) {
        JsonArray lineItems = new JsonArray();
        productDetails.forEach(product -> {
            JsonObject item = new JsonObject();
            item.addProperty("name", product.getProductName());
            item.addProperty("quantity", product.getQuantity());
            item.addProperty("sku", product.getProductId());
            item.addProperty("unit_price", Math.max(product.getPrice(), 500));
            lineItems.add(item);
        });
        return lineItems;
    }


    public void authenticate() {
        try {
            HttpResponse<String> response = callCourierAPI(authenticationURL, createRequestBody(""), getHeaderRequest());
            if (response.getStatus() == 200) {
                JsonObject responseBody = JsonParser.parseString(response.getBody()).getAsJsonObject();
                token = responseBody.getAsJsonObject("data")
                        .getAsJsonObject("response")
                        .get("token_id").getAsString();
            } else {
                log.error("Authentication failed: {}", response.getStatusText());
                token = null;
            }
        } catch (Exception e) {
            log.error("Error during authentication: {}", e.getMessage());
            token = null;
        }
    }

    public String bookShipment(PackedOrder packedOrder) {
        authenticate();
        try {
            HttpResponse<String> response = callCourierAPI(baseURL + bookingURL, createReturnRequestBody(packedOrder), getHeaderRequest());
            if (response.getStatus() == 200 && !isError(response)) {
                log.info("Shipment booked successfully: {}", response.getBody());
                packedOrder.setAwb(getAWBFromJsonResponse(response.getBody()));
                packedOrder.setCourierCompany(getCourierCompany(response.getBody()));
                return getLabelLink(response.getBody());
            } else {
                if (isTokenExpired(response)) {
                    authenticate();
                    return bookShipment(packedOrder);
                }
                log.error("Failed to book shipment: {}", getErrorMessage(response.getBody()));
                return "Failed";
            }
        } catch (Exception e) {
            log.error("Error during shipment booking: {}", e.getMessage());
            return "Error";
        }
    }

    private boolean isError(HttpResponse<String> response) {
        try {
            JsonObject responseJson = JsonParser.parseString(response.getBody()).getAsJsonObject();
            return responseJson.getAsJsonObject("data").get("error").getAsBoolean();
        } catch (Exception e) {
            log.error("Error checking response for errors: {}", e.getMessage());
            return true;
        }
    }

    private boolean isTokenExpired(HttpResponse<String> response) {
        try {
            JsonObject responseJson = JsonParser.parseString(response.getBody()).getAsJsonObject();
            JsonArray messageArray = responseJson.getAsJsonObject("data")
                    .getAsJsonObject("response")
                    .getAsJsonArray("message");
            for (int i = 0; i < messageArray.size(); i++) {
                JsonObject message = messageArray.get(i).getAsJsonObject();
                if ("Token Expired".equals(message.get("token_error").getAsString())) {
                    return true;
                }
            }
        } catch (Exception e) {
            log.error("Error checking token expiration: {}", e.getMessage());
        }
        return false;
    }

    private String getAWBFromJsonResponse(String jsonResponse) {
        try {
            JsonObject response = JsonParser.parseString(jsonResponse).getAsJsonObject();
            return response.getAsJsonObject("data")
                    .getAsJsonObject("response")
                    .get("airwaybilno").getAsString();
        } catch (Exception e) {
            log.error("Error extracting AWB from JSON response: {}", e.getMessage());
            return null;
        }
    }

    private String getLabelLink(String jsonResponse) {
        try {
            JsonObject response = JsonParser.parseString(jsonResponse).getAsJsonObject();
            return response.getAsJsonObject("data")
                    .get("dispatch_label_url").getAsString();
        } catch (Exception e) {
            log.error("Error extracting label link from JSON response: {}", e.getMessage());
            return null;
        }
    }

    private String getCourierCompany(String jsonResponse) {
        try {
            JsonObject response = JsonParser.parseString(jsonResponse).getAsJsonObject();
            return response.getAsJsonObject("data")
                    .getAsJsonObject("response")
                    .get("courier").getAsString();
        } catch (Exception e) {
            log.error("Error extracting courier company from JSON response: {}", e.getMessage());
            return null;
        }
    }

    private String getErrorMessage(String response) {
        if(response != null){
            JsonObject data = JsonParser.parseString(response).getAsJsonObject().getAsJsonObject("data");
            if(data  == null){
                return POSConstants.EMPTY;
            }
            JsonObject responseObj = data.getAsJsonObject("response");
            return responseObj == null ? POSConstants.EMPTY : responseObj.get("message").getAsString();
        }
        return POSConstants.EMPTY;
    }

}