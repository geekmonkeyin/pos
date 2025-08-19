package com.gkmonk.pos.services.externalapi;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class APIProxyService {


    public HttpResponse<String> executeGet(String url) throws UnirestException {
        Unirest.setTimeouts(0, 0);
        return Unirest.get(url)
                .asString();
    }

    public HttpResponse<String> executePost(String url, String body, Map<String,String> headers) throws UnirestException {
        Unirest.setTimeouts(0, 0);
        return Unirest.post(url)
                .headers(headers)
                .body(body)
                .asString();

    }

}
