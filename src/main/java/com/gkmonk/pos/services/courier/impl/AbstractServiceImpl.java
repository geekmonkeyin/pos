package com.gkmonk.pos.services.courier.impl;

import com.gkmonk.pos.services.courier.IOrdersSyncService;
import com.google.gson.JsonObject;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractServiceImpl implements IOrdersSyncService {


    public HttpResponse<String> callCourierAPI(String url, JsonObject requestBody, Map<String,String> headerRequest) throws Exception {
        Unirest.setTimeouts(0, 0);
        return Unirest.post(url)
                .headers(headerRequest)
                .body(requestBody.toString())
                .asString();
    }

    @Override
    public Map<String,String> getHeaderRequest(){
        Map<String,String> request = new HashMap<>();
        request.put(
                "Content-Type", "application/json");
        request.put(     "Accept", "application/json");
        return request;
    }

    public HttpResponse<String> callCourierAPIGet(String url, JsonObject jsonObject, Map<String, String> headerRequest) throws UnirestException {
        return Unirest.get(url)
                .asString();

    }
}
