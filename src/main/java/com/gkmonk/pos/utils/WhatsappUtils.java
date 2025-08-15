package com.gkmonk.pos.utils;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONObject;

public class WhatsappUtils {

    public static final String ACCOUNT_SID = "ACf4c9e3eef60b8c7bfbb65a51d1813b67";
    public static final String AUTH_TOKEN = "TmIwOTRKQ1ZfUmFKNms2STNJbjNnMkthTVBQTWxWTmpwOEtZNW5HT1FwSTo=";


    public static void sendPaymentReceipt(String vendorName,String number,Double amount,String date,String remarks) {
        try {
            addUser(number,vendorName);
        JSONObject requestBody = new JSONObject();
        JSONObject traits = new JSONObject();
        traits.put("date", date);
        traits.put("totalAmount", amount );
        traits.put("vendor", vendorName +"("+remarks+")");

        requestBody.put("fullPhoneNumber", PhoneUtils.formatPhoneNo(number));
        requestBody.put("event", "paymentreceived");
        requestBody.put("traits", traits);


            callApi(requestBody);
        } catch (Exception e) {
            System.out.println("Error sending whatsapp msg:"+e.getMessage());
        }

    }

    public static void callApi(JSONObject requestBody) throws UnirestException {
        Unirest.setTimeouts(0, 0);
        HttpResponse<String> response = Unirest.post("https://api.interakt.ai/v1/public/track/events/")
                .header("Authorization", "Basic " + AUTH_TOKEN).header("Content-Type", "application/json")
                .body(requestBody.toString()).asString();
    }

    public static void addUser(String to, String userName) {

        try {

            JSONObject userDetails = new JSONObject();
            userDetails.put("name", userName);

            JSONObject userEvent = new JSONObject();
            userEvent.put("fullPhoneNumber", to);
            userEvent.put("traits", userDetails);
            String urlString = "https://api.interakt.ai/v1/public/track/users/";
            // restTemplate.put(url, updatedOrder);
            ObjectMapper mapper = new ObjectMapper();
            mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

            com.mashape.unirest.http.HttpResponse<String> response = Unirest.post(urlString)
                    .header("Authorization", "Basic " + AUTH_TOKEN).header("Content-Type", "application/json")
                    .body(userEvent.toString()).asString();
            int responseStatus = response.getStatus();
            System.out.println("Status code: " + responseStatus);
            String updatedBook = response.getBody();
            System.out.println(updatedBook);

        } catch (Exception e) {
            System.out.println("Exception in NetClientGet:- " + e);
        }

    }

}
