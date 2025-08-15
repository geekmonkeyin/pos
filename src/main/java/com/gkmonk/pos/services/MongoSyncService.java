/*
package com.gkmonk.pos.services;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.ReplaceOptions;
import org.bson.Document;
import org.springframework.stereotype.Service;

@Service
public class MongoSyncService {

    private final MongoClient remoteClient = MongoClients.create("mongodb://localhost:27017");
    private final MongoClient localClient = MongoClients.create("mongodb://localhost:27017");

    private final String dbName = "pos_system";
    private final String collectionName = "carton_details";

    public void syncData() {
        MongoCollection<Document> remoteCollection = remoteClient
                .getDatabase(dbName)
                .getCollection(collectionName);

        MongoCollection<Document> localCollection = localClient
                .getDatabase(dbName)
                .getCollection(collectionName);

        FindIterable<Document> remoteDocs = remoteCollection.find();

        for (Document doc : remoteDocs) {
            Object id = doc.get("_id");
            localCollection.replaceOne(new Document("_id", id), doc, new ReplaceOptions().upsert(true));
        }

        System.out.println("Sync completed at: " + java.time.LocalTime.now());
    }
}
*/
