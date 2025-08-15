package com.gkmonk.pos.rules.model;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("courier_rules")
public class CourierRules {

    private String requestType;
    private List<Rule> rules;

    public List<Rule> getRules() {
        return rules;
    }

    public void setRules(List<Rule> rules) {
        this.rules = rules;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }




}
