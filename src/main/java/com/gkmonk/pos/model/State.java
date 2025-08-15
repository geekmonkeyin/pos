package com.gkmonk.pos.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document("state_code")
public class State {

    private String state;
    private String code;


    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

}
