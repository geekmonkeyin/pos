package com.gkmonk.pos.services.token;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AllCredentialsService {

    @Autowired
    private MongoTemplate mongoTemplate;

    private List<Map> credentials;

    public List<Map> getCredentials() {
        if (credentials == null) {
            credentials = mongoTemplate.findAll(Map.class, "all_credentials");
        }
        return credentials;
    }

    public Optional<String> getMongoUri() {
        for (Map credential : getCredentials()) {
            Optional<String> uri = resolveMongoUri(credential);
            if (uri.isPresent()) {
                return uri;
            }
        }
        return Optional.empty();
    }

    public Optional<String> getMongoDatabaseName() {
        for (Map credential : getCredentials()) {
            if (credential == null) {
                continue;
            }
            Object databaseName = credential.get("database")
                    != null ? credential.get("database")
                    : credential.get("db")
                            != null ? credential.get("db")
                            : credential.get("databaseName")
                                    != null ? credential.get("databaseName")
                                    : credential.get("dbname");
            if (databaseName instanceof String && StringUtils.hasText((String) databaseName)) {
                return Optional.of((String) databaseName);
            }
        }
        return Optional.empty();
    }

    static Optional<String> resolveMongoUri(Map credential) {
        if (credential == null) {
            return Optional.empty();
        }

        String[] candidateKeys = {

                "spring.data.mongodbcloud.uri"
        };

        for (String key : candidateKeys) {
            Object value = credential.get(key);
            if (value instanceof String && StringUtils.hasText((String) value)) {
                return Optional.of((String) value);
            }
        }

        return Optional.empty();
    }
}
