package com.gkmonk.pos.services.interakt;

import com.gkmonk.pos.model.notification.NotificationType;
import com.gkmonk.pos.services.token.AllCredentialsService;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;

@Service
public class InteraktServiceImpl {

    @Value("${whatsapp.api.url}")
    private String API_URL;
    private String AUTH_TOKEN ;
    @Value(("${whatsapp.api.adduser.url}"))
    private String ADD_USER_URL;
    @Autowired
    private AllCredentialsService credentialsService;


    @PostConstruct
    void init(){
        //AUTH_TOKEN = System.getenv().get("whatsapp.api.token");

    }


    public static void main(String[] args) {
        InteraktServiceImpl interaktService = new InteraktServiceImpl();
        interaktService.AUTH_TOKEN = "Basic TmIwOTRKQ1ZfUmFKNms2STNJbjNnMkthTVBQTWxWTmpwOEtZNW5HT1FwSTo=";
        interaktService.API_URL = "https://api.interakt.ai/v1/public/track/events/";
        System.out.println("Auth Token: " + interaktService.AUTH_TOKEN);
        interaktService.sendOrderStatusUpdates("IN_TRANSIT","User","90324786355","919845192845", "", "BLUEDART", "Prepaid", "", "http://tracking.url");
    }
    public boolean sendOrderStatusUpdates(String currentStatus,String name,String awb,String phoneno, String edd, String courier, String paymentMode, String productURL, String trackingURL) {


        try {
            JSONObject traits = new JSONObject()
                    .put("username", name)
                    .put("awb", awb)
                    .put("edd", edd)
                    .put("courier", courier)
                    .put("paymentMode", paymentMode)
                    .put("trackingURL", trackingURL)
                    .put("currentStatus", currentStatus);

            JSONObject payload = new JSONObject()
                    .put("fullPhoneNumber",phoneno )
                   // .put("fullPhoneNumber","919560772223" )

                    .put("event", "orderpickedandshipped")
                    .put("traits", traits);

            HttpResponse<String> response = callInteraktService(payload,API_URL);
            //send scam alert
            sendScamAlert(phoneno);
            return isEventCreated(response);
        } catch (Exception e) {
            System.err.println("API request failed: " + e.getMessage());
        }
        return false;
    }

    private void updateAuthToken() {
        if(AUTH_TOKEN == null){
            for ( Map credMap : credentialsService.getCredentials() ) {
                if("interakt".equalsIgnoreCase((String) credMap.get("_id"))){
                    AUTH_TOKEN = credMap.get("whatsapp.api.token").toString();
                }
            }
        }
    }

    private void sendScamAlert(String phoneno) throws UnirestException {
        JSONObject traits = new JSONObject();

        JSONObject payload = new JSONObject()
                .put("fullPhoneNumber",phoneno )
                // .put("fullPhoneNumber","919560772223" )

                .put("event", "scamalert")
                .put("traits", traits);

        HttpResponse<String> response = callInteraktService(payload,API_URL);

    }

    public boolean sendMsgOnProductShipped(String name,String awb,String phoneno, String edd, String courier, String paymentMode, String productURL, String trackingURL) {
        return sendOrderStatusUpdates(NotificationType.ORDER_PICKED.getValue(),name,awb,phoneno,edd,courier,paymentMode,productURL,trackingURL);

    }

    private boolean isEventCreated(HttpResponse<String> response) throws JSONException {
        JSONObject json = new JSONObject(response.getBody());

        boolean result = json.getBoolean("result");
        String message = json.getString("message");
        if("Event created successfully".equalsIgnoreCase(message) && result) {
            return true;
        } else {
            System.err.println("Failed to create event: " + message);
            return false;
        }
    }

    public HttpResponse<String> callInteraktService(JSONObject payload,String url) throws UnirestException {
        updateAuthToken();
        Unirest.setTimeouts(0, 0);
        return Unirest.post(url)
                .header("Content-Type", "application/json")
                .header("Authorization", AUTH_TOKEN)
                .body(payload.toString())
                .asString();

    }

    public boolean addUser(String name, String phoneNumber) {
        try {
            JSONObject payload = new JSONObject()
                    .put("fullPhoneNumber", phoneNumber)
                    .put("name", name);
            HttpResponse<String> response = callInteraktService(payload,ADD_USER_URL);
            return isEventCreated(response);
        } catch (Exception e) {
            System.err.println("API request failed: " + e.getMessage());
        }
        return false;
    }

    public boolean sendMsgOnProductInTransit(String name, String awb, String phoneno, String edd, String courier, String paymentMode, String productURL, String trackingURL) {
        return sendOrderStatusUpdates(NotificationType.IN_TRANSIT.getValue(),name,awb,phoneno,edd,courier,paymentMode,productURL,trackingURL);
    }

    public boolean sendMsgOnProductOutForDelivery(String name, String awb, String phoneno, String edd, String courier, String paymentMode, String productURL, String trackingURL) {
        return sendOrderStatusUpdates(NotificationType.OUT_FOR_DELIVERY.getValue(),name,awb,phoneno,edd,courier,paymentMode,productURL,trackingURL);

    }

    public boolean sendMsgOnProductDelivered(String name, String awb, String phoneno, String edd, String courier, String paymentMode, String productURL, String trackingURL) {
        return sendOrderStatusUpdates(NotificationType.DELIVERED.getValue(),name,awb,phoneno,edd,courier,paymentMode,productURL,trackingURL);
    }
}
