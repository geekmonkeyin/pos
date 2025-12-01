package com.gkmonk.pos.services.token;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AllCredentialsService {

    @Autowired
    private MongoTemplate mongoTemplate;

    private List<Map> credentials;

    public List<Map> getCredentials() {
        if(credentials == null) {
            credentials = mongoTemplate.findAll(Map.class, "all_credentials");
        }
        return credentials;
    }
}
