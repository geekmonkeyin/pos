package com.gkmonk.pos.model;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("state_code")
@Data
public class State {

    private String state;
    private String code;
    private String zoho_state_code;

}
